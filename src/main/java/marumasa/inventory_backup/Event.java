package marumasa.inventory_backup;

import marumasa.inventory_backup.command.Restore;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

import static marumasa.inventory_backup.Utils.allNull;

public class Event implements Listener {

    private static final Map<HumanEntity, ItemStack[]> InventoryBackup = new HashMap<>();


    private final Config cfg;
    private final Minecraft mc;

    public Event(Config config, Minecraft minecraft) {
        cfg = config;
        mc = minecraft;
    }

    @EventHandler
    public void onInventory(InventoryCloseEvent event) {

        // プレイヤー取得
        final HumanEntity player = event.getPlayer();
        // プレイヤーからインベントリ取得
        final PlayerInventory inventory = player.getInventory();
        // インベントリからアイテムの配列を取得
        final ItemStack[] itemStacks = inventory.getContents();

        // もし何もアイテムを持っていなかったら
        if (allNull(itemStacks) && InventoryBackup.containsKey(player)) {
            // インベントリをバックアップから復元するかどうかのメッセージを表示する
            player.spigot().sendMessage(Restore.generateRestoreMessage(itemStacks, cfg));
        } else {
            // インベントリのアイテムの情報をバックアップに追加
            InventoryBackup.put(player, inventory.getContents().clone());
        }
    }
}
