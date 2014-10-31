package me.libraryaddict.halloween;

import me.libraryaddict.HubConnector.HubApi;
import me.libraryaddict.Loader.RankLoader;
import me.libraryaddict.scoreboard.ScoreboardManager;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

public class Halloween extends JavaPlugin {

    public void onEnable() {
        HalloweenApi.setMainPlugin(this);
        Bukkit.getPluginManager().registerEvents(new HalloweenListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        HalloweenApi.getGameManager();
        HubApi.setPlayersAndTime(Bukkit.getOnlinePlayers().size(), 0);
        HubApi.registerListener(new SocketListener());
        RankLoader.setPlayerListColored(false);
        ScoreboardManager.setDisplayName(DisplaySlot.SIDEBAR, ChatColor.DARK_RED + "Trick " + ChatColor.BLUE + "or "
                + ChatColor.GOLD + "Treat");
        ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, ChatColor.BLUE + "Costume: ", 4);
        ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, ChatColor.DARK_AQUA + "Knocked: ", 3);
        ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, ChatColor.DARK_GRAY + "Repeats: ", 2);
        ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, ChatColor.GOLD + "Treats: ", 1);
        ScoreboardManager.makeScore(DisplaySlot.SIDEBAR, ChatColor.RED + "Tricks: ", 0);
    }

}
