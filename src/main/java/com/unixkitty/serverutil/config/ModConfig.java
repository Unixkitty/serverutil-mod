package com.unixkitty.serverutil.config;

import com.unixkitty.serverutil.ServerUtilMod;
import net.minecraftforge.common.config.Configuration;

public class ModConfig
{
    private static Configuration config;

    private static boolean showMOTD = true;

    public static void load()
    {
        config = new Configuration(ServerUtilMod.instance.getConfig(), ServerUtilMod.VERSION);

        showMOTD = config.getBoolean("showMOTD", "General", showMOTD, "Show server MOTDs on join");

        config.save();
    }

    public static boolean showMOTD()
    {
        return showMOTD;
    }
}
