package marumasa.inventory_backup;

import marumasa.inventory_backup.command.Restore;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

import static marumasa.inventory_backup.Utils.allNull;
import static marumasa.inventory_backup.Utils.cloneContents;

public class Event implements Listener {

    private static final Map<HumanEntity, ItemStack[]> InventoryRecording = new HashMap<>();


    private final Config cfg;

    public Event(Config config) {
        cfg = config;
    }

    @EventHandler
    public void onInventory(InventoryCloseEvent event) {
        // プレイヤー取得
        final HumanEntity player = event.getPlayer();
        // プレイヤーからインベントリ取得
        final PlayerInventory inventory = player.getInventory();
        // インベントリからアイテムの配列を取得
        final ItemStack[] itemStacks = inventory.getContents();

        ItemStack[] Backup = InventoryRecording.get(player);

        // もし何もアイテムを持っていなかったら
        if (allNull(itemStacks) && !allNull(Backup)) {
            // インベントリをバックアップから復元するかどうかのメッセージを表示する
            player.spigot().sendMessage(Restore.generateRestoreMessage(Backup, cfg));
        }

        // インベントリを複製して保存
        InventoryRecording.put(player, cloneContents(itemStacks));
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent event) {
        // プレイヤー取得
        final HumanEntity player = event.getPlayer();
        // プレイヤーからインベントリ取得
        final PlayerInventory inventory = player.getInventory();
        // インベントリからアイテムの配列を取得
        final ItemStack[] itemStacks = inventory.getContents();
        // インベントリを複製して保存
        InventoryRecording.put(player, cloneContents(itemStacks));
    }
}
