package marumasa.inventory_backup;

import marumasa.inventory_backup.command.Restore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static marumasa.inventory_backup.Utils.*;

// インベントリを復元の対象にするのは、アイテムが実際に失われたときだけにする
// (容器へ移したり、地面に捨てたりしたアイテムはワールドに残っているので、対象にしない)
public class Event implements Listener {

    private static final String CLEAR_PERMISSION = "minecraft.command.clear";

    private final Minecraft plugin;
    private final Restore restore;

    public Event(Minecraft plugin, Restore restore) {
        this.plugin = plugin;
        this.restore = restore;
    }

    @EventHandler
    public void onInventory(InventoryCloseEvent event) {// プレイヤーがインベントリを閉じると実行される
        // プレイヤー取得
        final HumanEntity player = event.getPlayer();
        // インベントリマネージャーを作成
        final InventoryManager manager = new InventoryManager(player);

        // もしクリエイティブの操作でアイテムがすべて消えていたら
        if (manager.isCreativeClear(event.getInventory())) {
            // インベントリをバックアップから復元するかどうかのメッセージを表示する
            restore.offer(player, manager.loadBackup(), true);
        }

        // インベントリをバックアップに保存
        manager.updateBackup();
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent event) {// プレイヤーがインベントリを開くと実行される
        // インベントリをバックアップに保存
        new InventoryManager(event.getPlayer()).updateBackup();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreativeClick(InventoryCreativeEvent event) {// クリエイティブのインベントリを操作すると実行される
        final ItemStack item = event.getCursor();
        // もしスロットが空にされたら
        if (event.getSlotType() != InventoryType.SlotType.OUTSIDE && (item == null || item.getType().isAir())) {
            InventoryManager.markSlotCleared(event.getWhoClicked().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {// プレイヤーがアイテムを捨てると実行される
        InventoryManager.markItemDropped(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {// プレイヤーのゲームモードが変わると実行される
        final Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.CREATIVE) new InventoryManager(player).saveBackup();
        else InventoryManager.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {// プレイヤーが死亡すると実行される
        if (event.getKeepInventory()) return;
        // 地面に落ちたアイテムは拾い直せるので、本人には復元させない (管理者が確認して復元する)
        offerIfEmptied(event.getEntity(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {// プレイヤーがコマンドを実行すると実行される
        onClearCommand(event.getPlayer(), event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(ServerCommandEvent event) {// コンソールからコマンドを実行すると実行される
        // コマンドブロックなどは対象にしない
        if (event.getSender() instanceof ConsoleCommandSender) onClearCommand(event.getSender(), event.getCommand());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(RemoteServerCommandEvent event) {// RCON からコマンドを実行すると実行される
        onClearCommand(event.getSender(), event.getCommand());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {// プレイヤーがサーバーから退出すると実行される
        final Player player = event.getPlayer();
        InventoryManager.remove(player.getUniqueId());
        restore.forget(player.getUniqueId());
    }

    // 権限のある実行者の /clear で中身が消えたら、復元の対象にする
    private void onClearCommand(CommandSender sender, String commandLine) {
        final String target = ClearCommand.parseTarget(commandLine);
        if (target == null || !sender.hasPermission(CLEAR_PERMISSION)) return;

        for (Player player : findPlayers(sender, target)) {
            // 他の人に消されたものは、本人には復元させない
            offerIfEmptied(player, player.equals(sender));
        }
    }

    private List<Player> findPlayers(CommandSender sender, String target) {
        final List<Player> players = new ArrayList<>();
        if (target.isEmpty()) {
            if (sender instanceof Player player) players.add(player);
            return players;
        }
        try {
            for (Entity entity : Bukkit.selectEntities(sender, target)) {
                if (entity instanceof Player player) players.add(player);
            }
        } catch (IllegalArgumentException e) {
            // 対象の指定が正しくなければ /clear も失敗するので、何もしない
        }
        return players;
    }

    // いまの中身を覚えておき、次の tick に空になっていたら失われたものとして復元の対象にする
    private void offerIfEmptied(Player player, boolean ownerAllowed) {
        final ItemStack[] contents = cloneContents(player.getInventory().getContents());
        if (isEmpty(contents)) return;

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && isEmpty(player.getInventory().getContents())) {
                restore.offer(player, contents, ownerAllowed);
            }
        });
    }
}
