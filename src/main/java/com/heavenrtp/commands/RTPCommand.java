package com.heavenrtp.commands;

import com.yech.heavenRTP.HeavenRTP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.yech.heavenRTP.RandomLocationGenerator.generateRandomLocation;

public class RTPCommand extends BukkitCommand {

    private final HeavenRTP plugin;

    public RTPCommand(HeavenRTP plugin) {
        super("rtp");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) return false;
            
        Location loc = generateRandomLocation(player.getWorld(), this.plugin.getConfig().getInt("range"));
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        String actionbarBeingTeleported = this.plugin.getConfig().getString("messages.actionbar-being-teleported");

        if (actionbarBeingTeleported == null) {
            actionbarBeingTeleported = "&cTeleporting...";
            this.plugin.getLogger().warning("Missing 'messages.actionbar-being-teleported' in config.yml. Using default message.");
        }

        actionbarBeingTeleported = actionbarBeingTeleported
                .replace("{x}", String.valueOf(x))
                .replace("{y}", String.valueOf(y))
                .replace("{z}", String.valueOf(z));

        Component actionBarTeleportMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(actionbarBeingTeleported);
        player.sendActionBar(actionBarTeleportMessage);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1F);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            player.teleportAsync(loc);
            player.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1F);
        });
        return false;
    }
}
