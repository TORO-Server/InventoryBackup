package marumasa.inventory_backup;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utils {

    // 配列にアイテムが 1 つも無ければ true を返すメソッド
    // 配列自体 null の場合 true を返す
    public static boolean isEmpty(ItemStack[] array) {
        if (array == null) return true;
        for (ItemStack itemStack : array) {
            if (itemStack != null && !itemStack.getType().isAir()) return false;
        }
        return true;
    }

    // プレイヤー自身のインベントリの画面か (容器を開いていないか)
    public static boolean isOwnInventory(Inventory topInventory) {
        final InventoryType type = topInventory.getType();
        return type == InventoryType.CRAFTING || type == InventoryType.CREATIVE;
    }

    public static Integer toInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static ItemStack[] cloneContents(ItemStack[] origin) {
        // コピー先の配列
        ItemStack[] copy = new ItemStack[origin.length];
        // 要素をcloneメソッドでコピー
        for (int i = 0; i < origin.length; i++) {
            copy[i] = cloneItemStack(origin[i]);
        }
        // コピーしたものを返す
        return copy;
    }

    // null でもエラーにならないクローン
    public static ItemStack cloneItemStack(ItemStack itemStack) {
        if (itemStack == null) return null;
        return itemStack.clone();
    }

    public static void sendMessage(HumanEntity player, BaseComponent text) {
        player.spigot().sendMessage(text);
    }
}
