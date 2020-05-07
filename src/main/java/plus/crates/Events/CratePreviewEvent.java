package plus.crates.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import plus.crates.Crate;
import plus.crates.CratesPlus;
import plus.crates.Winning;

import java.util.List;

public class CratePreviewEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private CratesPlus cratesPlus;
    private Player player;
    private Crate crate;
    private boolean canceled = false;
    private int page;

    public CratePreviewEvent(Player player, String crateName, CratesPlus cratesPlus, int page) {
        this.cratesPlus = cratesPlus;
        this.player = player;
        this.crate = cratesPlus.getConfigHandler().getCrates().get(crateName.toLowerCase());
        this.page = page;
    }

    public void doEvent() {
        if (!crate.isPreview())
            return; // Preview is disabled
        List<Winning> items = crate.getWinnings();
        Integer size = 54;
        int pages = 0;
        if (items.size() <= 9) {
            size = 9;
        } else if (items.size() <= 18) {
            size = 18;
        } else if (items.size() <= 27) {
            size = 27;
        } else if (items.size() <= 36) {
            size = 36;
        } else if (items.size() <= 45) {
            size = 45;
        } else if (items.size() > 54) {
        	pages = -Math.floorDiv(-items.size(), 45);
        }
        int i = 0;
        final Inventory inventory = Bukkit.createInventory(null, size, crate.getName(true) + " " + cratesPlus.getMessagesConfig().getString("Possible Wins Title"));
        if (pages > 1) {
            ItemStack nextButton = new ItemStack(Material.DARK_OAK_BUTTON);
            ItemMeta meta = nextButton.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Next Page (" + String.valueOf(page + 1) + ")");
            nextButton.setItemMeta(meta);
            ItemStack prevButton = new ItemStack(Material.BIRCH_BUTTON);
            meta = prevButton.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Previous Page (" + String.valueOf(page - 1) + ")");
            prevButton.setItemMeta(meta);
            if (page < pages) inventory.setItem(50, nextButton);
            if (page > 1) inventory.setItem(48, prevButton);
            int fromIndex = (page - 1) * 45;
            int toIndex = fromIndex + 45;
            for (Winning winning : items.subList(fromIndex, toIndex < items.size() ? toIndex : items.size())) {
                ItemStack itemStack = winning.getPreviewItemStack();
                if (itemStack == null)
                    continue;
                inventory.setItem(i, itemStack);
                i++;
            }
        }
        else {
            for (Winning winning : items) {
                ItemStack itemStack = winning.getPreviewItemStack();
                if (itemStack == null)
                    continue;
                inventory.setItem(i, itemStack);
                i++;
            }
        }
        for (int x = 0; x < inventory.getSize(); x++) {
        	if (inventory.getItem(x) == null) {
        		ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        		ItemMeta meta = filler.getItemMeta();
        		meta.setDisplayName(" ");
        		filler.setItemMeta(meta);
        		inventory.setItem(x, filler);
        	}
        }
        new BukkitRunnable() {
			@Override
			public void run() {
		        player.openInventory(inventory);
			}
		}.runTaskLater(cratesPlus, 1L);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Player getPlayer() {
        return this.player;
    }

    public CratesPlus getCratesPlus() {
        return cratesPlus;
    }

}