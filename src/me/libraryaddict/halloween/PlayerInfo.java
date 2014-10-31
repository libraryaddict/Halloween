package me.libraryaddict.halloween;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class PlayerInfo {
    private HashMap<CordPair, Long> knocked;
    private int tricks;
    private int treats;
    private int repeats;

    public PlayerInfo() {
        knocked = new HashMap<CordPair, Long>();
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

    public void addLooted(CordPair pair) {
        knocked.put(pair, System.currentTimeMillis());
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
