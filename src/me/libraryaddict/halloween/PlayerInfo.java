package me.libraryaddict.halloween;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.libraryaddict.holograms.Hologram;
import me.libraryaddict.holograms.Hologram.HologramTarget;

public class PlayerInfo {
    private static HashMap<CordPair, Hologram> hologram = new HashMap<CordPair, Hologram>();
    private HashMap<CordPair, Long> knocked;
    private int tricks;
    private int treats;
    private int repeats;

    public PlayerInfo() {
        knocked = new HashMap<CordPair, Long>();
    }

    private void check(CordPair pair) {
        if (!hologram.containsKey(pair)) {
            Location loc = new Location(HalloweenApi.getGameManager().getWorld(), pair.getX() + .5, 0, pair.getZ() + 0.5);
            for (int y = 0; y < 260; y++) {
                loc.add(0, 1, 0);
                if (loc.getBlock().getType() == Material.WOODEN_DOOR || loc.getBlock().getType() == Material.IRON_DOOR_BLOCK) {
                    break;
                }
            }
            Location loc2 = null;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || z != 0) {
                        Block b = loc.getBlock().getRelative(x, 0, z);
                        if (b.getType() == Material.WOODEN_DOOR || b.getType() == Material.IRON_DOOR_BLOCK) {
                            loc2 = b.getLocation().add(0.5, 0, 0.5);
                            break;
                        }
                    }
                }
            }
            if (loc2 != null) {
                loc = loc.clone().subtract(loc2).multiply(-0.5).add(loc.clone());
            }
            loc.add(0, 1.3, 0);
            hologram.put(pair, new Hologram(loc, ChatColor.GOLD + "Knocked").setTarget(HologramTarget.WHITELIST).start());
        }
    }

    public void startHolograms(Player player) {
        for (CordPair pair : knocked.keySet()) {
            if (knocked.get(pair) + (18 * 60 * 60 * 1000) > System.currentTimeMillis()) {
                check(pair);
                hologram.get(pair).addPlayer(player);
            }
        }
    }

    public PlayerInfo(int treats, int tricks, int repeats, HashMap<CordPair, Long> knocked) {
        this.knocked = knocked;
        this.treats = treats;
        this.tricks = tricks;
        this.repeats = repeats;
    }

    public void addTrick() {
        tricks++;
    }

    public void addTreat() {
        treats++;
    }

    public void addRepeat() {
        repeats++;
    }

    public void addLooted(Player player, CordPair pair) {
        knocked.put(pair, System.currentTimeMillis());
        check(pair);
        hologram.get(pair).addPlayer(player);
    }

    public void onQuit(Player player) {
        for (Hologram holo : hologram.values()) {
            holo.removePlayer(player);
        }
    }

    public String getLooted() {
        Iterator<Entry<CordPair, Long>> itel = knocked.entrySet().iterator();
        String s = "";
        while (itel.hasNext()) {
            Entry<CordPair, Long> entry = itel.next();
            s += entry.getKey().getX() + ":" + entry.getKey().getZ() + ":" + entry.getValue();
            if (itel.hasNext()) {
                s += ",";
            }
        }
        return s;
    }

    public int getTricks() {
        return tricks;
    }

    public int getTreats() {
        return treats;
    }

    public int getTimesLooted() {
        return getTricks() + getTreats();
    }

    public int getRepeats() {
        return repeats;
    }

    public boolean hasLooted(CordPair pair) {
        return knocked.containsKey(pair) && knocked.get(pair) + (18 * 60 * 60 * 1000) > System.currentTimeMillis();
    }

}
