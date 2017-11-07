package com.unixkitty.serverutil.config;

import com.unixkitty.serverutil.ServerUtilMod;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ModConfig
{
    private static Configuration config;

    private static boolean showMOTD = true;
    private static boolean registerTeleportCommand = true;

    public static void load()
    {
        config = new Configuration(new File(ServerUtilMod.instance.getConfigFolder(), ServerUtilMod.MODID + ".cfg"), ServerUtilMod.VERSION);

        showMOTD = config.getBoolean("showMOTD", "General", showMOTD, "Show server MOTDs on join");
        registerTeleportCommand = config.getBoolean("registerTeleportCommand", "General", registerTeleportCommand, "register /tpx command for inter-dimensional teleportation");

        config.save();
    }

    public static boolean showMOTD()
    {
        return showMOTD;
    }

    public static boolean registerTeleportCommand()
    {
        return registerTeleportCommand;
    }
}
