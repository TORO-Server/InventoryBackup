package marumasa.inventory_backup.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// 復元できるデータを、所有者ごとに最新の 1 件だけ保持する (取り出すと消える)
public class RestoreStore<T> {

    private record Entry<T>(int id, T value, boolean ownerAllowed) {
    }

    private final Map<UUID, Entry<T>> entries = new HashMap<>();
    private int nextId = 1;

    // データを登録して、復元コマンドで指定する番号を返す
    // ownerAllowed が false のものは管理者だけが取り出せる
    public int put(UUID owner, T value, boolean ownerAllowed) {
        final int id = nextId++;
        entries.put(owner, new Entry<>(id, value, ownerAllowed));
        return id;
    }

    // 所有者本人が番号を指定して取り出す。取り出せないときは null を返す
    public T takeOwn(UUID owner, int id, boolean admin) {
        final Entry<T> entry = entries.get(owner);
        if (entry == null || entry.id() != id || !(entry.ownerAllowed() || admin)) return null;
        entries.remove(owner);
        return entry.value();
    }

    // 管理者が所有者を指定して取り出す。無いときは null を返す
    public T takeForAdmin(UUID owner) {
        final Entry<T> entry = entries.remove(owner);
        return entry == null ? null : entry.value();
    }

    public void remove(UUID owner) {
        entries.remove(owner);
    }
}
