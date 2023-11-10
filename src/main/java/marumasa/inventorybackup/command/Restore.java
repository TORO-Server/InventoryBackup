package marumasa.inventorybackup.command;

import marumasa.inventorybackup.Config;
import marumasa.inventorybackup.Minecraft;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Restore implements CommandExecutor {

    public static String RestoreCommand(String key) {
        return String.format("/%s:restore %s", Minecraft.PluginName, key);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    public static TextComponent generateRestoreMessage(Config cfg) {



        TextComponent text = new TextComponent("あなたのインベントリにアイテムがありません。\nこのメッセージをクリックするとインベントリを復元できます。");
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("インベントリを復元する。")));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, RestoreCommand()));

        return text;
    }


    public static String commandRestore(ItemStack[] itemStacks) {
        return;
    }
}
