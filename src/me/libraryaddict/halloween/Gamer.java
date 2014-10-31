package me.libraryaddict.halloween;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.scoreboard.ScoreboardManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Gamer {
    private Costume costume;
    private int knocks;
    private long lastDoorbell;
    private long lastKnocked;
    private Player player;
    private PlayerInfo playerInfo;
    private long answerDelay;

    public boolean canKnock() {
        return answerDelay + 7200 < System.currentTimeMillis();
    }

    public Gamer(Player player2) {
        this.player = player2;
    }

    public Costume getCostume() {
        return costume;
    }

    public PlayerInfo getInfo() {
        return playerInfo;
    }

    // But as it should have loaded already. Error.

    public Player getPlayer() {
        return player;
    }

    public void loadData(Costume costume, PlayerInfo info) {
        this.costume = costume;
        this.playerInfo = info;
        if (costume != Costume.NO_DISGUISE) {
            Disguise disguise = costume.getDisguise().setEntity(getPlayer());
            disguise.getWatcher().setCustomName(getPlayer().getDisplayName());
            disguise.startDisguise();

        }
    }

    public boolean onDoorbell(final Location loc) {
        if (!canKnock())
            return false;
        if (lastDoorbell + 1500 > System.currentTimeMillis()) {
            return false;
        }
        lastDoorbell = System.currentTimeMillis();
        loc.getWorld().playSound(loc, Sound.NOTE_PIANO, 1.2F, 1.5F);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
            public void run() {
                loc.getWorld().playSound(loc, Sound.NOTE_PIANO, 1.2F, 1F);
            }
        }, 10);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
            public void run() {
                loc.getWorld().playSound(loc, Sound.NOTE_PIANO, 1.2F, 0.8F);
            }
        }, 20);
        answerDelay = System.currentTimeMillis();
        return true;
    }

    public boolean onKnock(Location loc) {
        if (!canKnock())
            return false;
        if (lastKnocked + 100 > System.currentTimeMillis())
            return false;
        if (knocks == 3 || lastKnocked + 500 < System.currentTimeMillis()) {
            knocks = 0;
        }
        lastKnocked = System.currentTimeMillis();
        World world = HalloweenApi.getGameManager().getWorld();
        world.playSound(loc, Sound.NOTE_BASS_DRUM, 1.2F, 1);
        if (++knocks == 3) {
            answerDelay = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void setCostume(Costume costume) {
        if (getCostume() != costume) {
            this.costume = costume;
            if (costume != Costume.NO_DISGUISE) {
                Disguise disguise = costume.getDisguise().setEntity(getPlayer());
                disguise.getWatcher().setCustomName(getPlayer().getDisplayName());
                disguise.startDisguise();
            } else {
                DisguiseAPI.undisguiseToAll(getPlayer());
            }
            ScoreboardManager.addToTeam(getPlayer(), ChatColor.BLUE + "Costume: ", "Costume", "", costume.getName(), false);
            HalloweenApi.getMysqlManager().changeDisguise(getPlayer().getUniqueId(), getCostume());
        }
    }
}
