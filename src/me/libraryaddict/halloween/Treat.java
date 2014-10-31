package me.libraryaddict.halloween;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.Currency.CreditsApi;
import me.libraryaddict.Loader.Rank;
import me.libraryaddict.Loader.RankLoader;

public enum Treat {
    CREDIT, CREDIT_1, CREDIT_2, CREDIT_3, CREDIT_4, CREDIT_5, POTIONS, VIP, VIP_1, VIP_2;
    public static void checkPet(Player p) {
        Gamer gamer = HalloweenApi.getPlayerManager().getGamer(p);
        if (gamer.getInfo().getTimesLooted() == 100) {
            p.sendMessage(ChatColor.GOLD + "100 trick or treats reached!");
            p.sendMessage(ChatColor.GOLD + "You have been given a pet witch to use in the hub!");
            final String name = p.getName();
            Bukkit.getScheduler().scheduleAsyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                public void run() {
                    try {
                        Connection con = me.libraryaddict.block.MysqlManager.getConnection();
                        Statement stmt = con.createStatement();
                        stmt.execute("INSERT INTO Pets (Owner, Pet) VALUES ('" + name + "', 'WITCH')");
                        stmt.close();
                        con.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    public void apply(Player p) {
        switch (this) {
        case CREDIT:
            int credits = new Random().nextInt(13) + 2;
            CreditsApi.giveCredits(p, credits, false);
            p.sendMessage(ChatColor.DARK_AQUA + "Given " + credits + " credits");
            break;
        case VIP:
            if (!Rank.VIP.hasRank(p) || Rank.VIP.getExpires(p.getName()) != 0) {
                long time = 1;
                String length = null;
                switch (new Random().nextInt(4)) {
                case 0:
                    time = 60 * 60;
                    length = "a hour";
                    break;
                case 1:
                    time = 60 * 60 * 6;
                    length = "6 hours";
                    break;
                case 2:
                    time = 60 * 60 * 12;
                    length = "12 hours";
                    break;
                case 3:
                    time = 30 * 60;
                    length = "30 minutes";
                    break;
                default:
                    break;
                }
                RankLoader.addRank(Rank.VIP, p.getName(), time);
                p.sendMessage(ChatColor.DARK_PURPLE + "Given " + length + " of VIP!");
            } else {
                CREDIT.apply(p);
            }
            break;
        case POTIONS:
            for (PotionEffectType type : new PotionEffectType[] { PotionEffectType.POISON, PotionEffectType.SLOW,
                    PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION }) {
                p.removePotionEffect(type);
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 120, 1));
            p.sendMessage("<Villager> Here, eat these strange looking candies..");
            break;
        default:
            if (this.name().contains("VIP_")) {
                VIP.apply(p);
            }
            if (this.name().contains("CREDIT_")) {
                CREDIT.apply(p);
            }
            break;
        }
    }
}
