package me.libraryaddict.halloween;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.ChatColor;

public enum NpcStatements {
    WITCH(new String[] { "*cackle* Guess someone wants a " + ChatColor.RED + "TRICK!",
            "Here's " + ChatColor.RED + "something" + ChatColor.WHITE + " to remember me by!",
            "You dare come to MY door!?! A " + ChatColor.RED + "trick" + ChatColor.WHITE + " for you!",
            "AHAHAHHA TIME FOR A " + ChatColor.RED + "TRICK!",
            "So? Where's my treat? Give me or I'll play a nasty " + ChatColor.RED + "trick" + ChatColor.WHITE + " on you!" },
            new String[] { "If you ain't going to stick around. Don't knock!", "Where'd he go!?! I wanted to make a pie!!",
                    "Yeah. Ring my doorbell? You better run!" }),

    VILLAGER(new String[] { "Hello child, here's a " + ChatColor.GOLD + "treat!",
            ChatColor.GOLD + "Treat " + ChatColor.RESET + "you say? Take it!",
            "Children these days are so adorable! Take a " + ChatColor.GOLD + "treat!",
            "I choose to give you a " + ChatColor.GOLD + "treat!" },
            new String[] { "Children these days.. No respect.. Running away as I answer the door.." }),

    REVISIT(new String[] { "You've been here! Push off!", "How many times have I seen you today..",
            "Don't you have better things to do then revisit me?" }, new String[] { "YEAH THATS RIGHT! U BETTER RUN!",
            "Where'd that brat go..", "Thats right! Run! I was going to give you a treat too..",
            "Yeah thats right! You better run!", "I've already seen you around here! Keep running!" });

    public enum Type {
        GIVE, RUNAWAY;
    }

    private ArrayList<String> give = new ArrayList<String>();
    private ArrayList<String> runaway = new ArrayList<String>();

    private NpcStatements(String[] give, String[] runaway) {
        this.give.addAll(Arrays.asList(give));
        this.runaway.addAll(Arrays.asList(runaway));
    }

    public String getMessage(NpcStatements.Type type) {
        if (type == NpcStatements.Type.GIVE) {
            return give.get(new Random().nextInt(give.size()));
        } else {
            return runaway.get(new Random().nextInt(runaway.size()));
        }
    }

}
