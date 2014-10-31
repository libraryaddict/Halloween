package me.libraryaddict.halloween;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import me.libraryaddict.inventory.ItemBuilder;
import me.libraryaddict.scoreboard.ScoreboardManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MysqlManager {
    private ItemStack chooseCostume = new ItemBuilder(Material.SKULL_ITEM, 1, (short) 2)
            .setTitle(ChatColor.GOLD + "Choose Costume")
            .addLore(
                    ChatColor.YELLOW + "Right click with this to open a menu to pick a costume! "
                            + "Each costume requires a level of experience to use. Gain experience by going trick and treating!",
                    30).build();

    public void changeDisguise(final UUID uuid, final Costume costume) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
            public void run() {
                try {
                    Connection con = me.libraryaddict.block.MysqlManager.getConnection();
                    Statement stmt = con.createStatement();
                    stmt.execute("UPDATE Halloween SET Costume='" + costume + "' WHERE uuid='" + uuid.toString() + "'");
                    stmt.close();
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void loadGamer(final Gamer gamer) {
        final UUID uuid = gamer.getPlayer().getUniqueId();
        Bukkit.getScheduler().scheduleAsyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
            public void run() {
                try {
                    Connection con = me.libraryaddict.block.MysqlManager.getConnection();
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM Halloween WHERE uuid='" + uuid.toString() + "'");
                    final Costume costume;
                    final PlayerInfo info;
                    final float exp;
                    final Collection<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
                    if (rs.first()) {
                        exp = rs.getFloat("Exp");
                        costume = Costume.valueOf(rs.getString("Costume"));
                        String looted = rs.getString("Looted");
                        String effects = rs.getString("Potions");
                        String[] potions = effects.split(",");
                        String[] doors = looted.split(",");
                        HashMap<CordPair, Long> lootedDoors = new HashMap<CordPair, Long>();
                        if (looted.length() > 0)
                            for (String door : doors) {
                                String[] split = door.split(":");
                                lootedDoors.put(new CordPair(Integer.parseInt(split[0]), Integer.parseInt(split[1])),
                                        Long.parseLong(split[2]));
                            }
                        if (effects.length() > 0)
                            for (String potion : potions) {
                                String[] split = potion.split(":");
                                potionEffects.add(new PotionEffect(PotionEffectType.getById(Integer.parseInt(split[0])), Integer
                                        .parseInt(split[1]), Integer.parseInt(split[2])));
                            }
                        info = new PlayerInfo(rs.getInt("Treats"), rs.getInt("Tricks"), rs.getInt("Repeats"), lootedDoors);
                        rs.close();
                    } else {
                        rs.close();
                        stmt.execute("INSERT INTO Halloween (uuid) VALUES ('" + uuid.toString() + "')");
                        info = new PlayerInfo();
                        costume = Costume.NO_DISGUISE;
                        exp = 0;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                        public void run() {
                            Player p = gamer.getPlayer();
                            p.setLevel((int) Math.floor(exp / 100));
                            p.setExp((exp - (p.getLevel() * 100)) / 100);
                            p.addPotionEffects(potionEffects);
                            gamer.loadData(costume, info);
                            p.sendMessage(ChatColor.GOLD + "================ " + ChatColor.DARK_RED + "Red" + ChatColor.RED
                                    + "Warfare " + ChatColor.RED + "presents" + ChatColor.GOLD + " ================");
                            p.sendMessage(ChatColor.GOLD + "=================== " + ChatColor.DARK_RED + "Trick "
                                    + ChatColor.BLUE + "or " + ChatColor.GOLD + "Treat" + ChatColor.GOLD + " ===================");
                            p.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "To play: " + ChatColor.AQUA
                                    + "Go door to door and knock (punch) 3 times, you can also use the doorbells! "
                                    + "You will receive a treat, and sometimes.. You will receive a nasty " + ChatColor.RED
                                    + "trick! " + ChatColor.AQUA
                                    + "The worst ones will take your credits and give you small bans!");
                            p.sendMessage(ChatColor.BLUE
                                    + "Can't find any fresh doors? Check back again in a day! They'll have restocked theirselves!");
                            p.getInventory().setItem(7, chooseCostume);
                            ScoreboardManager.addToTeam(p, ChatColor.BLUE + "Costume: ", "Costume", "", costume.getName(), false);
                            ScoreboardManager.addToTeam(p, ChatColor.DARK_AQUA + "Knocked: ", "Knocked", "", ChatColor.AQUA + ""
                                    + (info.getTimesLooted() + info.getRepeats()), false);
                            ScoreboardManager.addToTeam(p, ChatColor.DARK_GRAY + "Repeats: ", "Repeats", "", "" + ChatColor.GRAY
                                    + info.getRepeats(), false);
                            ScoreboardManager.addToTeam(p, ChatColor.GOLD + "Treats: ", "Treats", "", ChatColor.YELLOW + ""
                                    + info.getTreats(), false);
                            ScoreboardManager.addToTeam(p, ChatColor.RED + "Tricks: ", "Tricks", "", ChatColor.DARK_RED + ""
                                    + info.getTricks(), false);
                        }
                    });
                    stmt.close();
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void onQuit(Gamer gamer) {
        Player p = gamer.getPlayer();
        final String statement = "UPDATE Halloween SET Potions='" + serialize(p.getActivePotionEffects()) + "', Exp="
                + ((p.getLevel() + p.getExp()) * 100) + " WHERE uuid='" + p.getUniqueId() + "'";
        Bukkit.getScheduler().scheduleAsyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
            public void run() {
                try {
                    Connection con = me.libraryaddict.block.MysqlManager.getConnection();
                    Statement stmt = con.createStatement();
                    stmt.execute(statement);
                    stmt.close();
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private String serialize(Collection<PotionEffect> potionEffects) {
        Iterator<PotionEffect> itel = potionEffects.iterator();
        String s = "";
        while (itel.hasNext()) {
            PotionEffect effect = itel.next();
            s += effect.getType().getId() + ":" + effect.getDuration() + ":" + effect.getAmplifier();
            if (itel.hasNext()) {
                s += ",";
            }
        }
        return s;
    }

    public void onLoot(Player player, PlayerInfo info) {
        final String[] strings = new String[] {
                "UPDATE Halloween SET Looted='" + info.getLooted() + "' , Treats=" + info.getTreats() + ", Tricks="
                        + info.getTricks() + ", Repeats=" + info.getRepeats() + " WHERE uuid = '"
                        + player.getUniqueId().toString() + "'",
                "UPDATE Halloween SET Potions='" + serialize(player.getActivePotionEffects()) + "', Exp="
                        + ((player.getLevel() + player.getExp()) * 100) + " WHERE uuid='" + player.getUniqueId() + "'" };
        Bukkit.getScheduler().scheduleAsyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
            public void run() {
                try {
                    Connection con = me.libraryaddict.block.MysqlManager.getConnection();
                    Statement stmt = con.createStatement();
                    for (String string : strings)
                        stmt.execute(string);
                    stmt.close();
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
