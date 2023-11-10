package marumasa.inventory_backup;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class Utils {

    // 配列の要素がすべてnullだったらtrueを返すメソッド
    public static boolean allNull(Object[] array) {
        // streamを使って配列の要素を処理する
        return Arrays.stream(array)
                // 要素がnullでない場合はfalseを返す
                .noneMatch(Objects::nonNull);
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
}
