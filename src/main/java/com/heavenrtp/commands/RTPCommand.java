package com.heavenrtp.commands;

import com.yech.heavenRTP.HeavenRTP;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static com.yech.heavenRTP.RandomLocationGenerator.generateRandomLocation;

public class RTPCommand extends BukkitCommand {

    private final HeavenRTP plugin;
    private final Object2LongOpenHashMap<UUID> cooldowns = new Object2LongOpenHashMap<>();

    public RTPCommand(HeavenRTP plugin) {
        super("rtp");
        this.plugin = plugin;
        cooldowns.defaultReturnValue(-1L);
        this.setPermission("heaven.rtp");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) return false;

        int cooldownTime = this.plugin.getConfig().getInt("settings.cooldown");
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        long lastUsed = cooldowns.getLong(playerUUID);
        if (lastUsed != -1L) {
            long timeLeft = (lastUsed + (cooldownTime * 1000L)) - currentTime;

            if (timeLeft > 0) {
                String cooldownMessage = this.plugin.getConfig().getString("messages.cooldown");
                assert cooldownMessage != null;
                cooldownMessage = cooldownMessage.replace("{time}", String.valueOf(timeLeft / 1000));
                Component cooldownComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(cooldownMessage);
                player.sendMessage(cooldownComponent);
                return true;
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            cooldowns.put(playerUUID, currentTime);

            Location loc = generateRandomLocation(player.getWorld(), this.plugin.getConfig().getInt("settings.range"));
            int x = loc.getBlockX();
            int z = loc.getBlockZ();
            int y = loc.getBlockY();
            y = safeY(loc.getWorld(), x, y, z);


            String actionbarBeingTeleported = this.plugin.getConfig().getString("messages.actionbar-being-teleported");
            assert actionbarBeingTeleported != null;

            actionbarBeingTeleported = actionbarBeingTeleported
                    .replace("{x}", String.valueOf(x))
                    .replace("{y}", String.valueOf(y))
                    .replace("{z}", String.valueOf(z));

            Component actionBarTeleportMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(actionbarBeingTeleported);
            player.sendActionBar(actionBarTeleportMessage);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);

            player.teleportAsync(loc);
            player.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);
        });
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!(sender.hasPermission("heaven.rtp"))) {
                String noPermsMessage = this.plugin.getConfig().getString("messages.no-perms");
                assert noPermsMessage != null;
                Component noPermsComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(noPermsMessage);
                player.sendMessage(noPermsComponent);
            }
        });
        return true;
    }
    private int safeY(World world, int x, int y, int z) {
        List<Material> safeBlocks = this.plugin.getConfig().getStringList("settings.safe-blocks")
                .stream()
                .map(Material::valueOf)
                .toList();

        ObjectArrayList<Material> liquids = ObjectArrayList.of(Material.WATER, Material.LAVA);


        for (int offset = 1; offset <= 10; offset++) {

            int downY = y - offset;

            Material ground = getBlockType(world, x, downY, z);
            Material air1 = getBlockType(world, x, downY + 1, z);
            Material air2 = getBlockType(world, x, downY + 2, z);
            Material air3 = getBlockType(world, x, downY + 3, z);

            if (safeBlocks.contains(ground) && air1 == Material.AIR && air2 == Material.AIR && air3 == Material.AIR && !liquids.contains(ground) && !liquids.contains(air1) && !liquids.contains(air2) && !liquids.contains(air3)) {
                return downY + 1;
            }
        }
        return y;
    }

    private Material getBlockType(final World world, final int x, final int y, final int z) {
        return world.isChunkLoaded(x >> 4, z >> 4) ? world.getBlockAt(x, y, z).getType() : Material.STONE;
    }
}