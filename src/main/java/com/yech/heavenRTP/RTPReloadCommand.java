package com.yech.heavenRTP;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

public class RTPReloadCommand extends BukkitCommand {

    private final HeavenRTP plugin;

    public RTPReloadCommand(HeavenRTP plugin) {
        super("rtpreload");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        String configReloadMessage = this.plugin.getConfig().getString("messages.config-reloaded");
        assert configReloadMessage != null;
        String noPermsMessage = this.plugin.getConfig().getString("messages.no-perms");
        assert noPermsMessage != null;

        if (sender.hasPermission("heaven.rtpreload")) {
            Component reloadMessageComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(configReloadMessage);
            sender.sendMessage(reloadMessageComponent);

            plugin.reloadConfig();
        } else {
            Component noPermsComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(noPermsMessage);
            sender.sendMessage(noPermsComponent);
            return false;
        }
        return true;
    }
}
