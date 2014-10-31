package me.libraryaddict.halloween;

import java.util.ArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerManager {
    private ArrayList<Gamer> gamers = new ArrayList<Gamer>();

    public Gamer getGamer(Entity entity) {
        for (Gamer gamer : gamers) {
            if (gamer.getPlayer() == entity) {
                return gamer;
            }
        }
        return null;
    }

    public Gamer registerGamer(Player player) {
        Gamer gamer = new Gamer(player);
        HalloweenApi.getMysqlManager().loadGamer(gamer);
        gamers.add(gamer);
        return gamer;
    }

    public void unregisterGamer(Gamer gamer) {
        gamers.remove(gamer);
        HalloweenApi.getMysqlManager().onQuit(gamer);
    }
}
