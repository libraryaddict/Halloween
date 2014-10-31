package me.libraryaddict.halloween;

import java.util.Random;

import me.libraryaddict.Currency.CreditsApi;
import me.libraryaddict.ban.BanApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum Trick {
    BAN, BLINDNESS, CREDIT_LOSS, NAUSEA, POISON, SLOW;

    public void apply(final Player p) {
        switch (this) {
        case CREDIT_LOSS:
            int credits = new Random().nextInt(15) + 1;
            CreditsApi.takeCredits(p, credits);
            p.sendMessage(ChatColor.RED + "You've lost " + credits + " credits!");
            break;
        case SLOW:
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 40, 1), true);
            break;
        case POISON:
            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 40, 1), true);
            break;
        case BLINDNESS:
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 40, 1), true);
            break;
        case NAUSEA:
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 40, 1), true);
            break;
        case BAN:
            final String banLength;
            final long time;
            switch (new Random().nextInt(3)) {
            case 0:
                banLength = "3 minutes";
                time = 60 * 3;
                break;
            case 1:
                banLength = "5 minutes";
                time = 60 * 5;
                break;
            case 2:
                banLength = "10 minutes";
                time = 60 * 10;
                break;
            default:
                banLength = "1 minute";
                time = 60;
                break;
            }
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "Uh oh! " + p.getName() + ChatColor.DARK_RED
                    + " triggered the worst trick! A ban for " + banLength + "!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                public void run() {
                    BanApi.banUUID(p.getUniqueId().toString(), "Evil Witch", "Received a nasty trick while trick or treating!",
                            time);
                }
            }, 50);
        default:
            break;
        }
    }
}
