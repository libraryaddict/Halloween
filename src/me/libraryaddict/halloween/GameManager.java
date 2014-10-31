package me.libraryaddict.halloween;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class GameManager {
    private int nextLightning = new Random().nextInt(120) + 60;
    private World world = Bukkit.getWorlds().get(0);

    public GameManager() {
        getWorld().setGameRuleValue("doDaylightCycle", "false");
        getWorld().setGameRuleValue("doMobSpawning", "true");
        getWorld().setTime(15000);
        getWorld().setWeatherDuration(999999);
        getWorld().setPVP(false);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HalloweenApi.getMainPlugin(), new Runnable() {
            private long lastSecond;

            public void run() {
                if (lastSecond + 1000 <= System.currentTimeMillis()) {
                    lastSecond = System.currentTimeMillis();
                    onTick();
                }
            }
        }, 1, 1);
    }

    public World getWorld() {
        return world;
    }

    public void onTick() {
        if (--nextLightning <= 0) {
            nextLightning = new Random().nextInt(120) + 60;
            getWorld().strikeLightningEffect(
                    getWorld().getHighestBlockAt(
                            getWorld().getSpawnLocation().clone()
                                    .add(new Random().nextInt(200) - 100, 0, new Random().nextInt(200) - 100)).getLocation());
            getWorld().setWeatherDuration(999999);
        }
    }
}
