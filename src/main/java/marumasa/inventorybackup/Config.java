package marumasa.inventorybackup;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public final int test;

    public Config(final Minecraft plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        test = config.getInt("test.value");
    }
}
