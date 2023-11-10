package marumasa.inventory_backup;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public final String restore_command, chat_text, hover_text;

    public Config(final Minecraft plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        chat_text = config.getString("chat-text");
        hover_text = config.getString("hover-text");
        restore_command = config.getString("restore-command");
    }
}
