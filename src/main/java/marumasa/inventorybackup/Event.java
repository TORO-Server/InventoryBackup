package marumasa.inventorybackup;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

import static marumasa.inventorybackup.Utils.cloneInventory;

public class Event implements Listener {

    private static final Map<HumanEntity, ItemStack[]> InventoryBackup = new HashMap<>();


    private final Config cfg;
    private final Minecraft mc;

    public Event(Config config, Minecraft minecraft) {
        cfg = config;
        mc = minecraft;
    }

    @EventHandler
    public void onInventory(InventoryCloseEvent event) {

        final HumanEntity player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        final ItemStack[] itemStacks = inventory.getContents();

        for (ItemStack i : itemStacks)
            player.sendMessage(i.toString());


        // バックアップに追加
        InventoryBackup.put(player, inventory.getContents().clone());


        mc.getServer().broadcastMessage(player.getName());

        inventory.setContents(InventoryBackup.get(player));
    }
}
