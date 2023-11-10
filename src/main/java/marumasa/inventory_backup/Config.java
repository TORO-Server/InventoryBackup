package marumasa.inventory_backup;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public final String restore_command;

    public Config(final Minecraft plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        restore_command = config.getString("restore-command");
    }
}
