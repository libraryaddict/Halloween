package me.libraryaddict.halloween;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.inventory.events.PagesClickEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(PagesClickEvent event) {
        if (event.getName().equals("CostumeChooser")) {
            ItemStack item = event.getItemStack();
            if (item != null && item.hasItemMeta()) {
                Costume costume = null;
                for (Costume cos : Costume.values()) {
                    if (cos.getItem().equals(item)) {
                        costume = cos;
                        break;
                    }
                }
                if (costume != null) {
                    Player p = event.getPlayer();
                    Gamer gamer = HalloweenApi.getPlayerManager().getGamer(p);
                    if (gamer.getCostume() != costume) {
                        if (p.getLevel() >= costume.getLevelRequired()) {
                            gamer.setCostume(costume);
                            if (costume != Costume.NO_DISGUISE) {
                                Disguise disguise = costume.getDisguise().setEntity(p);
                                disguise.getWatcher().setCustomName(p.getDisplayName());
                                disguise.startDisguise();
                                p.sendMessage(ChatColor.BLUE + "Now using the " + costume.getName() + " costume!");
                            } else {
                                p.sendMessage(ChatColor.BLUE + "You've taken off your costume!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You don't have enough experience to enquip that costume!");
                        }
                    } else {
                        if (costume == Costume.NO_DISGUISE)
                            p.sendMessage(ChatColor.RED + "You don't have a costume on!");
                        else
                            p.sendMessage(ChatColor.RED + "You are already using that!");
                    }
                }
            }
        }
    }
}
