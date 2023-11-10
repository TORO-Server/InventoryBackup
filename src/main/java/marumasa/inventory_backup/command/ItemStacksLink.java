package marumasa.inventory_backup.command;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ItemStacksLink {
    private static final Set<ItemStack[]> ItemStacksSet = new HashSet<>();

    public static void add(ItemStack[] itemStacks) {
        ItemStacksSet.add(itemStacks);
    }

    public static ItemStack[] get(int hashcode) {

        for (ItemStack[] itemStacks : ItemStacksSet)
            // ハッシュコードが同じだったら それを返す
            if (Arrays.hashCode(itemStacks) == hashcode) return itemStacks;

        // 見つからなかったら null を 返す
        return null;
    }
}
