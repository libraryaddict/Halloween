package me.libraryaddict.halloween;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import me.libraryaddict.HubConnector.HubApi;
import me.libraryaddict.Loader.Rank;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.CreeperWatcher;
import me.libraryaddict.halloween.NpcStatements.Type;
import me.libraryaddict.hub.HubReturn;
import me.libraryaddict.inventory.ItemBuilder;
import me.libraryaddict.inventory.PageInventory;
import me.libraryaddict.inventory.PageLayout;
import me.libraryaddict.scoreboard.ScoreboardManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HalloweenListener implements Listener {

    private ItemStack chooseCostume = new ItemBuilder(Material.SKULL_ITEM, 1, (short) 2)
            .setTitle(ChatColor.GOLD + "Choose Costume")
            .addLore(
                    ChatColor.YELLOW + "Right click with this to open a menu to pick a costume! "
                            + "Each costume requires a level of experience to use. Gain experience by going trick and treating!",
                    30).build();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != SpawnReason.CUSTOM) {
            event.setCancelled(true);
            Location loc = event.getLocation();
            if (loc.getBlock().getY() >= loc.getWorld().getHighestBlockYAt(loc)
                    && HalloweenApi.getGameManager().getWorld().getEntitiesByClass(Bat.class).size() < 50) {
                switch (new Random().nextInt(10)) {
                case 0:
                    Disguise disguise = new MobDisguise(DisguiseType.CREEPER);
                    ((CreeperWatcher) disguise.getWatcher()).setPowered(true);
                    disguise.getWatcher().setInvisible(true);
                    DisguiseAPI.disguiseNextEntity(disguise);
                    break;
                case 1:
                    DisguiseAPI.disguiseNextEntity(new MiscDisguise(DisguiseType.WITHER_SKULL));
                    break;
                default:
                    break;
                }
                event.getEntity().getLocation().getWorld().spawnCreature(loc, EntityType.BAT);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(EntityInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
        } else if (((Player) event.getEntity()).getHealth() - event.getFinalDamage() <= 0 || event.getCause() == DamageCause.FALL) {
            event.setCancelled(true);
        }
        if (event.getCause() == DamageCause.VOID) {
            event.setCancelled(true);
            Location center = HalloweenApi.getGameManager().getWorld().getSpawnLocation();
            double angle = ((2 * Math.PI) / 50) * new Random().nextInt(50);
            double x = 23 * Math.cos(angle);
            double z = 23 * Math.sin(angle);
            Location loc = center.clone().add(x, 0, z);
            float yaw = (float) Math
                    .abs(((Math.atan2(center.getX() - loc.getX(), center.getZ() - loc.getZ()) * 180) / Math.PI) - 180);
            while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
                loc.add(0, -1, 0);
            loc.setYaw(yaw);
            event.getEntity().teleport(loc);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    private boolean isDoor(Material mat) {
        return mat == Material.IRON_DOOR_BLOCK || mat == Material.WOODEN_DOOR;
    }

    private void onInteract(final Gamer gamer, Block b) {
        boolean isBell = false;
        boolean isDoor = false;
        if (isDoor(b.getType())) {
            if (isDoor(b.getRelative(BlockFace.DOWN).getType())) {
                b = b.getRelative(BlockFace.DOWN);
            }
            isDoor = true;
        } else if (b.getType().name().contains("BUTTON")) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || z != 0) {
                        Block a = b.getRelative(x, 0, z);
                        if (isDoor(a.getType())) {
                            if (isDoor(a.getRelative(BlockFace.DOWN).getType())) {
                                a = a.getRelative(BlockFace.DOWN);
                            }
                            b = a;
                            isBell = true;
                            isDoor = true;
                            break;
                        }
                    }
                }
            }
        }
        if (isDoor) {
            final Player p = gamer.getPlayer();
            if (gamer.getInfo() == null) {
                p.sendMessage(ChatColor.RED + "Your data hasn't loaded yet!");
                return;
            }
            for (int x = -1; x < 1; x++) {
                for (int z = -1; z < 1; z++) {
                    Block c = b.getRelative(x, 0, z);
                    if (c.getType() == b.getType()) {
                        b = c;
                        break;
                    }
                }
            }
            if (isBell ? gamer.onDoorbell(b.getLocation()) : gamer.onKnock(b.getLocation())) {
                Block otherDoor = null;
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x != 0 || z != 0) {
                            Block c = b.getRelative(x, 0, z);
                            if (c.getType() == b.getType()) {
                                otherDoor = c;
                            }
                        }
                    }
                }
                Location loc = b.getLocation().add(0.5, 0, 0.5);
                final Location npcLoc;
                if (otherDoor != null) {
                    npcLoc = loc.clone().subtract(otherDoor.getLocation().add(0.5, 0, 0.5)).multiply(-0.5).add(loc.clone());
                } else {
                    npcLoc = loc;
                }
                final Block b1 = b;
                final Block b2 = otherDoor;
                final CordPair door = new CordPair(b1.getX(), b1.getZ());
                final boolean giveLoot = !gamer.getInfo().hasLooted(door);
                final boolean isTreat = gamer.getInfo().getTreats() / 4 < gamer.getInfo().getTricks()
                        || new Random().nextInt(4) != 0;
                Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                    public void run() {
                        final Entity npc;
                        if (giveLoot) {
                            if (isTreat) {
                                npc = npcLoc.getWorld().spawnEntity(npcLoc, EntityType.VILLAGER);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                                    public void run() {
                                        if (!npc.hasMetadata("Ran")) {
                                            gamer.getInfo().addTreat();
                                            gamer.getInfo().addLooted(p, door);
                                            HalloweenApi.getMysqlManager().onLoot(p, gamer.getInfo());
                                            p.sendMessage("<Villager> " + NpcStatements.VILLAGER.getMessage(Type.GIVE));
                                            Treat.values()[new Random().nextInt(Treat.values().length)].apply(p);
                                            Treat.checkPet(p);
                                            ScoreboardManager.addToTeam(p, ChatColor.GOLD + "Treats: ", "Treats", "",
                                                    ChatColor.YELLOW + "" + gamer.getInfo().getTreats(), false);
                                            ScoreboardManager.addToTeam(p, ChatColor.DARK_AQUA + "Knocked: ", "Knocked", "",
                                                    ChatColor.AQUA
                                                            + ""
                                                            + (gamer.getInfo().getTreats() + gamer.getInfo().getTricks() + gamer
                                                                    .getInfo().getRepeats()), false);
                                            float exp = p.getExp();
                                            exp += 0.075;
                                            if (exp >= 1) {
                                                exp -= 1;
                                                p.setLevel(p.getLevel() + 1);
                                                p.playSound(p.getLocation(), Sound.LEVEL_UP, 9999, 0);
                                            }
                                            p.setExp(exp);
                                        } else {
                                            p.sendMessage("<Villager> " + NpcStatements.VILLAGER.getMessage(Type.RUNAWAY));
                                        }
                                    }
                                }, 40);
                            } else {
                                npc = npcLoc.getWorld().spawnEntity(npcLoc, EntityType.WITCH);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                                    public void run() {
                                        if (!npc.hasMetadata("Ran")) {
                                            float exp = p.getExp();
                                            exp += 0.075;
                                            if (exp >= 1) {
                                                exp -= 1;
                                                p.setLevel(p.getLevel() + 1);
                                                p.playSound(p.getLocation(), Sound.LEVEL_UP, 9999, 0);
                                            }
                                            p.setExp(exp);
                                            gamer.getInfo().addTrick();
                                            gamer.getInfo().addLooted(p, door);
                                            HalloweenApi.getMysqlManager().onLoot(p, gamer.getInfo());
                                            p.sendMessage("<Witch> " + NpcStatements.WITCH.getMessage(Type.GIVE));
                                            Trick.values()[new Random().nextInt(Trick.values().length)].apply(p);
                                            Treat.checkPet(p);
                                            ScoreboardManager.addToTeam(p, ChatColor.RED + "Tricks: ", "Tricks", "",
                                                    ChatColor.DARK_RED + "" + gamer.getInfo().getTricks(), false);
                                            ScoreboardManager.addToTeam(p, ChatColor.DARK_AQUA + "Knocked: ", "Knocked", "",
                                                    ChatColor.AQUA
                                                            + ""
                                                            + (gamer.getInfo().getTreats() + gamer.getInfo().getTricks() + gamer
                                                                    .getInfo().getRepeats()), false);
                                        } else {
                                            p.sendMessage("<Witch> " + NpcStatements.WITCH.getMessage(Type.RUNAWAY));
                                        }
                                    }
                                }, 40);
                            }
                        } else {
                            npc = npcLoc.getWorld().spawnEntity(npcLoc, EntityType.WITCH);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                                public void run() {
                                    if (!npc.hasMetadata("Ran")) {
                                        gamer.getInfo().addRepeat();
                                        gamer.getInfo().addLooted(p, door);
                                        HalloweenApi.getMysqlManager().onLoot(p, gamer.getInfo());
                                        ScoreboardManager.addToTeam(p, ChatColor.DARK_AQUA + "Knocked: ", "Knocked", "",
                                                ChatColor.AQUA
                                                        + ""
                                                        + (gamer.getInfo().getTreats() + gamer.getInfo().getTricks() + gamer
                                                                .getInfo().getRepeats()), false);
                                        ScoreboardManager.addToTeam(p, ChatColor.DARK_GRAY + "Repeats: ", "Repeats", "", ""
                                                + ChatColor.GRAY + gamer.getInfo().getRepeats(), false);
                                        p.sendMessage("<Witch> " + NpcStatements.REVISIT.getMessage(Type.GIVE));
                                    } else {
                                        p.sendMessage("<Witch> " + NpcStatements.REVISIT.getMessage(Type.RUNAWAY));
                                    }
                                }
                            }, 40);
                        }
                        ((LivingEntity) npc).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 99999));
                        ((LivingEntity) npc).setNoDamageTicks(50000);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                            public void run() {
                                if (p.getLocation().distance(npcLoc) < 6.5) {
                                    p.sendMessage("<" + p.getDisplayName() + ChatColor.RESET + "> " + ChatColor.RED + "Trick "
                                            + ChatColor.YELLOW + "or" + ChatColor.DARK_GREEN + " treat!");
                                } else {
                                    npc.setMetadata("Ran", new FixedMetadataValue(HalloweenApi.getMainPlugin(), ""));
                                }
                            }
                        }, 10);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                            public void run() {
                                npcLoc.getWorld().playSound(npcLoc, Sound.DOOR_OPEN, 1.2F, 1);
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (player.getLocation().distance(npcLoc) < 30) {
                                        for (int i = 0; i <= 1; i++) {
                                            player.sendBlockChange(b1.getRelative(0, i, 0).getLocation(), Material.AIR, (byte) 0);
                                            if (b2 != null) {
                                                player.sendBlockChange(b2.getRelative(0, i, 0).getLocation(), Material.AIR,
                                                        (byte) 0);
                                            }
                                        }
                                    }
                                }
                            }
                        }, 2);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
                            public void run() {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (player.getLocation().distance(npcLoc) < 30) {
                                        for (int i = 0; i <= 1; i++) {
                                            Block b = b1.getRelative(0, i, 0);
                                            player.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                                            if (b2 != null) {
                                                b = b2.getRelative(0, i, 0);
                                                player.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                                            }
                                        }
                                    }
                                }
                                npcLoc.getWorld().playSound(npcLoc, Sound.DOOR_CLOSE, 1.2F, 1);
                                npc.remove();
                            }
                        }, 80);
                    }
                }, 30);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();
        if (b == null || (b.getType() != Material.LEVER && b.getType() != Material.FENCE_GATE)) {
            event.setCancelled(true);
        }
        Gamer gamer = HalloweenApi.getPlayerManager().getGamer(event.getPlayer());
        if (b != null
                && event.getAction() == (b.getType().name().contains("BUTTON") ? Action.RIGHT_CLICK_BLOCK
                        : Action.LEFT_CLICK_BLOCK)) {
            onInteract(gamer, b);
        }
        if (event.getItem() != null && event.getItem().equals(chooseCostume) && event.getAction().name().contains("RIGHT")) {
            PageInventory inv = new PageInventory("CostumeChooser", event.getPlayer());
            ArrayList<Costume> costumes = new ArrayList<Costume>();
            for (Costume cos : Costume.values()) {
                costumes.add(cos);
            }
            Collections.sort(costumes, new Comparator<Costume>() {
                @Override
                public int compare(Costume o1, Costume o2) {
                    return new Integer(o1.getLevelRequired()).compareTo(o2.getLevelRequired());
                }

            });
            ArrayList<ItemStack> items = new ArrayList<ItemStack>();
            for (Costume cos : costumes) {
                items.add(cos.getItem());
            }
            inv.setTitle(ChatColor.GOLD + "Halloween Costumes");
            inv.setPages(new PageLayout("XXXXOXXXX", "XXXOXOXXX", "XXOXOXOXX", "XOXOXOXOX").generate(items));
            inv.openInventory();
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
        if (event.getRightClicked() instanceof ItemFrame) {
            Block b = event.getRightClicked().getLocation().getBlock();
            if (b.getType().name().contains("BUTTON")) {
                this.onInteract(HalloweenApi.getPlayerManager().getGamer(event.getPlayer()), b);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        HalloweenApi.getPlayerManager().registerGamer(p);
        p.getInventory().setItem(8, HubReturn.getHubIcon());
        ScoreboardManager.registerScoreboard(p);
        Location center = HalloweenApi.getGameManager().getWorld().getSpawnLocation();
        double angle = ((2 * Math.PI) / 50) * new Random().nextInt(50);
        double x = 23 * Math.cos(angle);
        double z = 23 * Math.sin(angle);
        Location loc = center.clone().add(x, 0, z);
        float yaw = (float) Math
                .abs(((Math.atan2(center.getX() - loc.getX(), center.getZ() - loc.getZ()) * 180) / Math.PI) - 180);
        while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
            loc.add(0, -1, 0);
        loc.setYaw(yaw);
        p.teleport(loc);
        Rank rank = Rank.getHighestRankWithDisplay(p);
        if (rank != null && rank.getRankWorth() >= Rank.VIP.getRankWorth()) {
            ScoreboardManager.addToTeam(p, rank.getRankName(), rank.getDisplayName(), false);
        }
        HubApi.setPlayersAndTime(Bukkit.getOnlinePlayers().size(), 0);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Gamer gamer = HalloweenApi.getPlayerManager().getGamer(event.getPlayer());
        HalloweenApi.getPlayerManager().unregisterGamer(gamer);
        gamer.getInfo().onQuit(gamer.getPlayer());
        ScoreboardManager.removeFromTeam(gamer.getPlayer());
        HubApi.setPlayersAndTime(Bukkit.getOnlinePlayers().size() - 1, 0);
        final File file = new File("world/playerdata", event.getPlayer().getUniqueId().toString() + ".dat");
        Bukkit.getScheduler().scheduleSyncDelayedTask(HalloweenApi.getMainPlugin(), new Runnable() {
            public void run() {
                file.delete();
            }
        }, 2);
    }
}
