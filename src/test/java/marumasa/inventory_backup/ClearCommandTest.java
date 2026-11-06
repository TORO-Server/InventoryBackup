package marumasa.inventory_backup;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClearCommandTest {

    @Test
    void parsesCommandWithoutTarget() {
        assertEquals("", ClearCommand.parseTarget("/clear"));
        assertEquals("", ClearCommand.parseTarget("/minecraft:clear"));
        assertEquals("", ClearCommand.parseTarget("clear"));
    }

    @Test
    void parsesTarget() {
        assertEquals("Steve", ClearCommand.parseTarget("/clear Steve"));
        assertEquals("@a", ClearCommand.parseTarget("/CLEAR  @a  minecraft:dirt 1"));
        assertEquals("Steve", ClearCommand.parseTarget("minecraft:clear Steve"));
    }

    @Test
    void ignoresOtherCommands() {
        assertNull(ClearCommand.parseTarget("/clearlag"));
        assertNull(ClearCommand.parseTarget("/say clear"));
        assertNull(ClearCommand.parseTarget("/other:clear"));
    }
}
