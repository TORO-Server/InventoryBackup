package marumasa.inventory_backup.command;

import marumasa.inventory_backup.Config;
import marumasa.inventory_backup.Minecraft;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static marumasa.inventory_backup.Utils.*;

public class Restore implements CommandExecutor {
    // 自分のインベントリを復元できる権限
    private static final String PERMISSION = "inventory_backup.restore";
    // 他のプレイヤーのインベントリと、死亡時のインベントリも復元できる権限
    private static final String PERMISSION_OTHERS = "inventory_backup.restore.others";

    private final Minecraft plugin;
    private final Config cfg;
    // 復元できるインベントリ (所有者ごとに 1 件。1 度復元すると消える)
    private final RestoreStore<ItemStack[]> store = new RestoreStore<>();

    public Restore(Minecraft plugin, Config config) {
        this.plugin = plugin;
        cfg = config;
    }

    // プレイヤーが実行する復元コマンドを生成
    public static String RestoreCommand(Config cfg, int id) {
        return String.format(cfg.restore_command, id);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /restore player <名前>
        if (args.length == 2 && args[0].equalsIgnoreCase("player")) return restoreOther(sender, args[1]);
        // /restore <番号>
        if (args.length == 1 && sender instanceof Player player) return restoreOwn(player, args[0]);
        return false;
    }

    // プレイヤーが復元コマンドを実行したら、このメソッドが実行される
    private boolean restoreOwn(Player sender, String arg) {
        // コマンドから番号を抽出する
        final Integer id = toInt(arg);
        if (id == null) return false;

        if (!canRestoreTo(sender, sender)) return true;

        // 番号からアイテム配列を取得する
        final ItemStack[] itemStacks = store.takeOwn(sender.getUniqueId(), id, sender.hasPermission(PERMISSION_OTHERS));
        if (itemStacks == null) {
            sender.sendMessage("§c復元できるインベントリがありません。");
            return true;
        }

        // コマンドを実行したプレイヤーのインベントリを復元する
        sender.getInventory().setContents(itemStacks);
        sender.sendMessage("§aインベントリを復元しました。");
        plugin.getLogger().info(sender.getName() + " restored their inventory");
        return true;
    }

    // 管理者が他のプレイヤーのインベントリを復元する
    private boolean restoreOther(CommandSender sender, String name) {
        if (!sender.hasPermission(PERMISSION_OTHERS)) {
            sender.sendMessage("§c他のプレイヤーのインベントリを復元する権限がありません。");
            return true;
        }

        final Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            sender.sendMessage("§cそのプレイヤーはオンラインではありません。");
            return true;
        }

        if (!canRestoreTo(sender, target)) return true;

        final ItemStack[] itemStacks = store.takeForAdmin(target.getUniqueId());
        if (itemStacks == null) {
            sender.sendMessage("§c復元できるインベントリがありません。");
            return true;
        }

        target.getInventory().setContents(itemStacks);
        target.sendMessage("§aインベントリが復元されました。");
        sender.sendMessage("§a" + target.getName() + " のインベントリを復元しました。");
        plugin.getLogger().info(sender.getName() + " restored the inventory of " + target.getName());
        return true;
    }

    // 復元先のインベントリの状態を確認する (復元できないときはデータを残したまま拒否する)
    private static boolean canRestoreTo(CommandSender sender, Player target) {
        // 死亡中に入れたアイテムはリスポーンで失われる
        if (target.isDead()) {
            sender.sendMessage("§cリスポーンしてから復元してください。");
            return false;
        }
        // 持っているアイテムを上書きしない
        if (!isEmpty(target.getInventory().getContents())) {
            sender.sendMessage("§cインベントリが空でないため復元できません。");
            return false;
        }
        return true;
    }

    // 失われたインベントリを復元の対象にして、復元できるプレイヤーにはメッセージを表示する
    public void offer(HumanEntity owner, ItemStack[] itemStacks, boolean ownerAllowed) {
        final int id = store.put(owner.getUniqueId(), cloneContents(itemStacks), ownerAllowed);

        if (owner.hasPermission(PERMISSION_OTHERS) || (ownerAllowed && owner.hasPermission(PERMISSION))) {
            sendMessage(owner, generateRestoreMessage(id));
        }
    }

    // 所有者のインベントリのデータを破棄する
    public void forget(UUID owner) {
        store.remove(owner);
    }

    private TextComponent generateRestoreMessage(int id) {
        // 表示するメッセージを作成してそれを返す
        TextComponent text = new TextComponent(cfg.chat_text);
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(cfg.hover_text)));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, RestoreCommand(cfg, id)));
        return text;
    }
}
