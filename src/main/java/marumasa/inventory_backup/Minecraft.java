package marumasa.inventory_backup;

import marumasa.inventory_backup.command.Restore;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minecraft extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Config 読み込み
        final Config config = new Config(this);
        // コマンドの処理
        final Restore restore = new Restore(this, config);
        // イベント登録
        getServer().getPluginManager().registerEvents(new Event(this, restore), this);
        // コマンドの処理 登録
        getCommand("restore").setExecutor(restore);
        // 同じ権限を他のプラグインが別の既定で登録していても、すべてのプラグインが有効になった後で op に揃える
        getServer().getScheduler().runTask(this, () -> {
            final Permission permission = getServer().getPluginManager().getPermission("inventory_backup.restore");
            if (permission != null) permission.setDefault(PermissionDefault.OP);
        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
