package com.unixkitty.serverutil;

import com.unixkitty.serverutil.command.*;
import com.unixkitty.serverutil.config.ModConfig;
import com.unixkitty.serverutil.proxy.CommonProxy;
import com.unixkitty.serverutil.util.TranslationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = ServerUtilMod.MODID, name = ServerUtilMod.NAME, version = ServerUtilMod.VERSION, acceptedMinecraftVersions = "[1.12]", acceptableRemoteVersions = "*")
public class ServerUtilMod
{
    public static final String MODID = "serverutil";
    public static final String NAME = "ServerUtil";
    //MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH
    public static final String VERSION = "1.12.2-0.0.7.1-dev";

    public static final Logger log = LogManager.getLogger(NAME);

    private File configFolder;

    @Mod.Instance(MODID)
    public static ServerUtilMod instance;

    @SidedProxy(serverSide = "com.unixkitty.serverutil.proxy.CommonProxy", clientSide = "com.unixkitty.serverutil.proxy.ClientProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        configFolder = new File(event.getModConfigurationDirectory(), ServerUtilMod.MODID);
        ModConfig.load();

        TranslationHandler.loadTranslations();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ModConfig.showMOTD())
        {
            MinecraftForge.EVENT_BUS.register(CommandMOTD.instance);
        }
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        if (ModConfig.registerTeleportCommand())
        {
            event.registerServerCommand(new CommandDimensionTeleport());
        }
        event.registerServerCommand(new CommandDimensionList());
        event.registerServerCommand(CommandMOTD.instance);
        event.registerServerCommand(CommandModBugs.instance);
        event.registerServerCommand(new CommandPlayerID());
        event.registerServerCommand(new CommandRegionCoord());
    }

    public File getConfigFolder()
    {
        return configFolder;
    }
}
