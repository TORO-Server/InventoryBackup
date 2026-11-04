package marumasa.inventory_backup.command;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RestoreStoreTest {
    private final RestoreStore<String> store = new RestoreStore<>();
    private final UUID owner = UUID.randomUUID();
    private final UUID other = UUID.randomUUID();

    @Test
    void ownerCanTakeOnlyOnce() {
        final int id = store.put(owner, "items", true);

        assertEquals("items", store.takeOwn(owner, id, false));
        assertNull(store.takeOwn(owner, id, false));
    }

    @Test
    void anotherPlayerCannotUseTheOwnersId() {
        final int id = store.put(owner, "items", true);

        assertNull(store.takeOwn(other, id, false));
        assertNull(store.takeOwn(other, id, true));
        assertEquals("items", store.takeOwn(owner, id, false));
    }

    @Test
    void wrongIdKeepsTheEntry() {
        final int id = store.put(owner, "items", true);

        assertNull(store.takeOwn(owner, id + 1, false));
        assertEquals("items", store.takeOwn(owner, id, false));
    }

    @Test
    void newEntryReplacesTheOldOne() {
        final int oldId = store.put(owner, "old", true);
        final int newId = store.put(owner, "new", true);

        assertNotEquals(oldId, newId);
        assertNull(store.takeOwn(owner, oldId, false));
        assertEquals("new", store.takeOwn(owner, newId, false));
    }

    @Test
    void adminOnlyEntryIsRefusedToTheOwner() {
        final int id = store.put(owner, "items", false);

        assertNull(store.takeOwn(owner, id, false));
        assertEquals("items", store.takeOwn(owner, id, true));
    }

    @Test
    void adminTakesByOwnerOnlyOnce() {
        store.put(owner, "items", false);

        assertEquals("items", store.takeForAdmin(owner));
        assertNull(store.takeForAdmin(owner));
    }

    @Test
    void removeForgetsTheEntry() {
        final int id = store.put(owner, "items", true);

        store.remove(owner);

        assertNull(store.takeOwn(owner, id, false));
        assertNull(store.takeForAdmin(owner));
    }
}
