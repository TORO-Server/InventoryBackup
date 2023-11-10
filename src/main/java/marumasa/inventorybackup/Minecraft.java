package marumasa.inventorybackup;

import org.bukkit.plugin.java.JavaPlugin;

public final class Minecraft extends JavaPlugin {

    @Override
    public void onEnable() {

        // Config 読み込み
        final Config config = new Config(this);
        // イベント登録
        getServer().getPluginManager().registerEvents(new Event(config, this), this);

        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
