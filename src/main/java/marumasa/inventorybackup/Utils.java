package marumasa.inventorybackup;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class Utils {
    protected static Inventory cloneInventory(Inventory origin) {

        // インベントリ作成
        final Inventory clone = Bukkit.createInventory(origin.getHolder(), 36);


        // 元のインベントリのアイテムをクローンのインベントリにコピー
        clone.setContents(origin.getContents());

        // 複製されたインベントリを返す
        return clone;
    }

    // 配列の要素がすべてnullだったらtrueを返すメソッド
    public static boolean allNull(Object[] array) {
        // streamを使って配列の要素を処理する
        return Arrays.stream(array)
                // 要素がnullでない場合はfalseを返す
                .noneMatch(Objects::nonNull);
    }

}
