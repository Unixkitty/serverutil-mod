package com.unixkitty.serverutil.config;

import com.unixkitty.serverutil.ServerUtilMod;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ModConfig
{
    private static Configuration config;

    public static boolean showMOTD = true;
    public static boolean enableMOTDInSingleplayer = false;
    public static boolean showModBugsAsMOTD = false;
    public static boolean non_ops_motd_can_optout = true;

    public static boolean non_ops_mod_bugs_add = false;
    public static boolean non_ops_mod_bugs_update = false;
    public static boolean non_ops_mod_bugs_remove = false;

    public static boolean registerTeleportCommand = true;

    public static void load()
    {
        config = new Configuration(new File(ServerUtilMod.instance.getConfigFolder(), ServerUtilMod.MODID + ".cfg"), ServerUtilMod.VERSION);

        showMOTD = config.getBoolean("showOnJoin", "MOTD", showMOTD, "Global show server MOTDs to players on join");
        enableMOTDInSingleplayer = config.getBoolean("enableMOTDInSingleplayer", "MOTD", enableMOTDInSingleplayer, "Show MOTD in Singleplayer, can be useful for LAN servers");
        showModBugsAsMOTD = config.getBoolean("showModBugsAsMOTD", "MOTD", showModBugsAsMOTD, "Should mod bug list be sent to players after motd");
        non_ops_motd_can_optout = config.getBoolean("canUsersOptout", "MOTD", non_ops_motd_can_optout, "Can non-ops opt themselves out from receiving MOTD on join");

        non_ops_mod_bugs_add = config.getBoolean("canUsersAdd", "mod_bugs", non_ops_mod_bugs_add, "Can non-ops add bugs");
        non_ops_mod_bugs_remove = config.getBoolean("canUsersRemove", "mod_bugs", non_ops_mod_bugs_remove, "Can non-ops remove bugs");
        non_ops_mod_bugs_update = config.getBoolean("canUsersUpdate", "mod_bugs", non_ops_mod_bugs_update, "Can non-ops update existing bugs");

        registerTeleportCommand = config.getBoolean("registerTeleportCommand", "General", registerTeleportCommand, "register /tpx command for inter-dimensional teleportation");

        config.save();
    }
}
