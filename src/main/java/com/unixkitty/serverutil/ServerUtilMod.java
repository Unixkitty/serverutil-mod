package com.unixkitty.serverutil;

import com.unixkitty.serverutil.command.DimensionTeleportCommand;
import com.unixkitty.serverutil.motd.ServerMOTD;
import com.unixkitty.serverutil.util.PropertyFileHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ServerUtilMod.MODID, version = ServerUtilMod.VERSION, acceptedMinecraftVersions = "[1.12]")
public class ServerUtilMod
{
    public static final String MODID = "serverutil";
    public static final String NAME = "ServerUtil";
    //MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH
    public static final String VERSION = "1.12-1.0.0.0-dev";

    public static Logger log = LogManager.getLogger(NAME);

    public static PropertyFileHandler propHandler = new PropertyFileHandler();

    @Mod.Instance(MODID)
    public static ServerUtilMod instance;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new ServerMOTD());
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new DimensionTeleportCommand());
    }
}
