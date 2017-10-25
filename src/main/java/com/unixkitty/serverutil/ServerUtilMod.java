package com.unixkitty.serverutil;

import com.unixkitty.serverutil.command.CommandDimensionList;
import com.unixkitty.serverutil.command.CommandDimensionTeleport;
import com.unixkitty.serverutil.command.CommandMOTD;
import com.unixkitty.serverutil.command.CommandModBugs;
import com.unixkitty.serverutil.config.ModConfig;
import com.unixkitty.serverutil.proxy.CommonProxy;
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

@Mod(modid = ServerUtilMod.MODID, name = ServerUtilMod.NAME, version = ServerUtilMod.VERSION, acceptedMinecraftVersions = "[1.12]")
public class ServerUtilMod
{
    public static final String MODID = "serverutil";
    public static final String NAME = "ServerUtil";
    //MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH
    public static final String VERSION = "1.12.2-0.0.2.2-dev";

    public static Logger log = LogManager.getLogger(NAME);

    private File config;

    private final CommandMOTD motdHandler = new CommandMOTD();
    private final CommandModBugs commandModBugs = new CommandModBugs();

    @Mod.Instance(MODID)
    public static ServerUtilMod instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = event.getSuggestedConfigurationFile();
        ModConfig.load();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ModConfig.showMOTD())
        {
            MinecraftForge.EVENT_BUS.register(motdHandler);
        }
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandDimensionTeleport());
        event.registerServerCommand(new CommandDimensionList());
        event.registerServerCommand(motdHandler);
        event.registerServerCommand(commandModBugs);
    }

    @SidedProxy(serverSide = "com.unixkitty.serverutil.proxy.CommonProxy", clientSide = "com.unixkitty.serverutil.proxy.ClientProxy")
    public static CommonProxy proxy;

    public File getConfig()
    {
        return config;
    }
}
