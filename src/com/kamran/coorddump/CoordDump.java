package com.kamran.coorddump;

import com.kamran.coorddump.commands.CoordDumpCommands;
import org.bukkit.plugin.java.JavaPlugin;

public class CoordDump extends JavaPlugin {

    private static CoordDump plugin;

    @Override
    public void onEnable() {
        plugin = this;
        plugin.saveDefaultConfig();
        getCommand("coorddump").setExecutor(new CoordDumpCommands());
    }

    @Override
    public void onDisable() {

    }

    public static CoordDump getPlugin() {
        return plugin;
    }

}
