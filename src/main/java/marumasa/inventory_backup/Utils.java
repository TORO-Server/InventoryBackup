package marumasa.inventory_backup;

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
}
