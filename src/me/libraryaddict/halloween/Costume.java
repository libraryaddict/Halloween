package me.libraryaddict.halloween;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;
import me.libraryaddict.inventory.ItemBuilder;

public enum Costume {
    BLAZE(new ItemStack(Material.BLAZE_POWDER), ChatColor.RED + "Blaze", new MobDisguise(DisguiseType.BLAZE), 8),

    CREEPER(new ItemStack(Material.MONSTER_EGG, 1, EntityType.CREEPER.getTypeId()), ChatColor.GREEN + "Creeper", new MobDisguise(
            DisguiseType.CREEPER), 7),

    ENDERMAN(new ItemStack(Material.ENDER_PEARL), ChatColor.LIGHT_PURPLE + "Enderman", new MobDisguise(DisguiseType.ENDERMAN), 10),

    GHOST(new ItemStack(Material.GHAST_TEAR), ChatColor.WHITE + "Ghost", new MobDisguise(DisguiseType.CREEPER), 9),

    SATAN(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), ChatColor.DARK_RED + "Satan", new PlayerDisguise("Satan"), 4),

    NO_DISGUISE(new ItemStack(Material.STRING), ChatColor.WHITE + "None", null, 0),

    NOTCH(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), ChatColor.DARK_PURPLE + "Notch", new PlayerDisguise("Notch"), 6),

    PIG_ZOMBIE(new ItemStack(Material.GOLD_NUGGET), ChatColor.GOLD + "Pig Zombie", new MobDisguise(DisguiseType.PIG_ZOMBIE), 3),

    SKELETON(new ItemStack(Material.BONE), ChatColor.DARK_GRAY + "Skeleton", new MobDisguise(DisguiseType.SKELETON), 2),

    WITCH(new ItemStack(Material.POTION, 1, (short) 16396), ChatColor.LIGHT_PURPLE + "Witch",
            new MobDisguise(DisguiseType.WITCH), 5),

    ZOMBIE(new ItemStack(Material.SKULL_ITEM, 1, (short) 2), ChatColor.DARK_GREEN + "Zombie",
            new MobDisguise(DisguiseType.ZOMBIE), 1);

    static {
        GHOST.disguise.getWatcher().setInvisible(true);
        ((CreeperWatcher) GHOST.disguise.getWatcher()).setPowered(true);
    }

    private Disguise disguise;
    private ItemStack item;
    private int levelRequired;
    private String name;

    private Costume(ItemStack item, String name, Disguise disguise, int levelRequired) {
        this.levelRequired = levelRequired;
        this.disguise = disguise;
        if (disguise != null) {
            disguise.getWatcher().setCustomNameVisible(true);
        }
        this.name = name;
        this.item = new ItemBuilder(item).setTitle(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Disguise: " + getName())
                .addLore(ChatColor.GOLD + "Level required: " + ChatColor.YELLOW + getLevelRequired()).build();
    }

    public Disguise getDisguise() {
        return disguise.clone();
    }

    public ItemStack getItem() {
        return item;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public String getName() {
        return name;
    }
}
