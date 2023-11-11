package marumasa.inventory_backup;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

import static marumasa.inventory_backup.Utils.cloneContents;

public class InventoryManager {

    // プレイヤーのインベントリのバックアップを管理している
    private static final Map<HumanEntity, ItemStack[]> InventoryBackup = new HashMap<>();

    private final HumanEntity player;
    public final PlayerInventory inventory;
    public final ItemStack[] itemStacks;

    public InventoryManager(HumanEntity player) {
        //プレイヤー 代入
        this.player = player;
        // プレイヤーからインベントリ取得
        this.inventory = player.getInventory();
        // インベントリからアイテムの配列を取得
        this.itemStacks = inventory.getContents();
    }

    public void saveBackup() {
        // インベントリをバックアップ
        InventoryBackup.put(player, cloneContents(itemStacks));
    }

    public ItemStack[] loadBackup() {
        // インベントリをバックアップ
        return InventoryBackup.get(player);
    }
}
