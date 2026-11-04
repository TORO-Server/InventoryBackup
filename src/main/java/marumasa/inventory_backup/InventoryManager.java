package marumasa.inventory_backup;

import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static marumasa.inventory_backup.Utils.cloneContents;
import static marumasa.inventory_backup.Utils.isEmpty;

// クリエイティブのプレイヤーのインベントリのバックアップを管理している
public class InventoryManager {

    // プレイヤーのインベントリのバックアップ (サーバーから退出したら削除する)
    private static final Map<UUID, ItemStack[]> InventoryBackup = new HashMap<>();
    // バックアップの後に、クリエイティブのインベントリでスロットを空にしたプレイヤー
    private static final Set<UUID> SlotCleared = new HashSet<>();
    // バックアップの後に、アイテムをワールドへ捨てたプレイヤー
    private static final Set<UUID> ItemDropped = new HashSet<>();

    private final HumanEntity player;
    private final UUID uuid;
    public final PlayerInventory inventory;
    public final ItemStack[] itemStacks;

    public InventoryManager(HumanEntity player) {
        //プレイヤー 代入
        this.player = player;
        this.uuid = player.getUniqueId();
        // プレイヤーからインベントリ取得
        this.inventory = player.getInventory();
        // インベントリからアイテムの配列を取得
        this.itemStacks = inventory.getContents();
    }

    public void saveBackup() {
        // インベントリをバックアップ
        InventoryBackup.put(uuid, cloneContents(itemStacks));
        SlotCleared.remove(uuid);
        ItemDropped.remove(uuid);
    }

    // クリエイティブのプレイヤーだけバックアップする
    public void updateBackup() {
        if (player.getGameMode() == GameMode.CREATIVE) saveBackup();
        else remove(uuid);
    }

    public ItemStack[] loadBackup() {
        // バックアップされているインベントリのアイテムを取得
        return InventoryBackup.get(uuid);
    }

    // 閉じたインベントリの状況から、クリエイティブの操作でアイテムが消えたかどうかを判定する
    public boolean isCreativeClear(Inventory closedInventory) {
        return player.getGameMode() == GameMode.CREATIVE
                // 容器を開いていたときは、アイテムが容器へ移っただけかもしれない
                && Utils.isOwnInventory(closedInventory)
                // クラフト欄に残っているアイテムは、閉じるとインベントリへ戻る
                && isEmpty(closedInventory.getContents())
                && SlotCleared.contains(uuid)
                && !ItemDropped.contains(uuid)
                && isEmpty(itemStacks)
                && !isEmpty(loadBackup());
    }

    public static void markSlotCleared(UUID uuid) {
        SlotCleared.add(uuid);
    }

    public static void markItemDropped(UUID uuid) {
        ItemDropped.add(uuid);
    }

    public static void remove(UUID uuid) {
        InventoryBackup.remove(uuid);
        SlotCleared.remove(uuid);
        ItemDropped.remove(uuid);
    }
}
