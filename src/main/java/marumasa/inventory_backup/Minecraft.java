package marumasa.inventory_backup;

import marumasa.inventory_backup.command.Restore;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minecraft extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Config 読み込み
        final Config config = new Config(this);
        // イベント登録
        getServer().getPluginManager().registerEvents(new Event(config, this), this);
        // コマンドの処理 登録
        getCommand("restore").setExecutor(new Restore());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
