package marumasa.inventory_backup;

import marumasa.inventory_backup.command.Restore;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static marumasa.inventory_backup.Utils.*;

public class Event implements Listener {


    private final Config cfg;

    public Event(Config config) {
        cfg = config;
    }

    @EventHandler
    public void onInventory(InventoryCloseEvent event) {
        // プレイヤー取得
        final HumanEntity player = event.getPlayer();
        // インベントリマネージャーを作成
        final InventoryManager manager = new InventoryManager(player);

        // バックアップされているインベントリのアイテムを取得
        final ItemStack[] backupContents = manager.loadBackup();
        // もし何もアイテムを持っていなかったら
        if (allNull(manager.itemStacks) && !allNull(backupContents)) {
            // インベントリをバックアップから復元するかどうかのメッセージを表示する
            sendMessage(player, Restore.generateRestoreMessage(backupContents, cfg));
        }

        //インベントリをバックアップに保存
        manager.saveBackup();
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent event) {
        // プレイヤー取得
        final HumanEntity player = event.getPlayer();
        // インベントリマネージャーを作成
        final InventoryManager manager = new InventoryManager(player);
        // インベントリをバックアップに保存
        manager.saveBackup();
    }
}
