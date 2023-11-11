package marumasa.inventory_backup.command;

import marumasa.inventory_backup.Config;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static marumasa.inventory_backup.Utils.toInt;

public class Restore implements CommandExecutor {

    // プレイヤーが実行する復元コマンドを生成
    public static String RestoreCommand(Config cfg, int hashcode) {
        return String.format(cfg.restore_command, hashcode);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) return onCommand(player, args);
        return false;
    }

    // プレイヤーが復元コマンドを実行したら、このメソッドが実行される
    private static boolean onCommand(Player sender, String[] args) {
        if (args.length != 1) return false;

        // コマンドからハッシュコードを抽出する
        Integer hashcode = toInt(args[0]);
        if (hashcode == null) return false;

        // ハッシュコードからアイテム配列を取得する
        ItemStack[] itemStacks = ItemStacksLink.get(hashcode);
        if (itemStacks == null) return false;

        // コマンドを実行したプレイヤーのインベントリを復元する
        sender.getInventory().setContents(itemStacks);
        return true;
    }

    public static TextComponent generateRestoreMessage(ItemStack[] itemStacks, Config cfg) {
        // ハッシュコード取得
        int hashcode = Arrays.hashCode(itemStacks);

        ItemStacksLink.add(itemStacks);

        // 表示するメッセージを作成してそれを返す
        TextComponent text = new TextComponent(cfg.chat_text);
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(cfg.hover_text)));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, RestoreCommand(cfg, hashcode)));
        return text;
    }
}
