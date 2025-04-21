package com.yech.heavenRTP;

import com.heavenrtp.commands.RTPCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HeavenRTP extends JavaPlugin {

    private static final Logger logger = Logger.getLogger(HeavenRTP.class.getName());
    private CommandMap commandMap;

    @Override
    public void onEnable() {
        RTPCommand rtpCommand = new RTPCommand(this);

        registerCommand(rtpCommand);

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void initCommandMap() {
        if (commandMap == null) {
            try {
                Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.log(Level.SEVERE, "Error accessing commandMap", e);
            }
        }
    }

    private void registerCommand(BukkitCommand command) {
        initCommandMap();
        if (commandMap != null) {
            commandMap.register(command.getName(), command);
        } else {
            logger.log(Level.SEVERE, "CommandMap is null, cannot register command: " + command.getName());
        }
    }
}
