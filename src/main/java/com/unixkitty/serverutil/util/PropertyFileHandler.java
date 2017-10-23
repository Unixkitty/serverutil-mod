package com.unixkitty.serverutil.util;

import com.unixkitty.serverutil.ServerUtilMod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyFileHandler
{
    private Properties properties_motd = new Properties();
    public final File file_motd = new File("DimensionTeleportCommand.properties");

    private OutputStream output = null;

    final String welcome_message_example = "Welcome to the server! This is an example MOTD.";
    final String motd_text_example = "Example information";

    public PropertyFileHandler()
    {
        if (!file_motd.exists())
        try
        {
            output = new FileOutputStream("DimensionTeleportCommand.properties");

            properties_motd = loadDefaults("DimensionTeleportCommand");

            properties_motd.store(output, "Properties file by mod " + ServerUtilMod.MODID);
        }
        catch (IOException e)
        {
            //TODO
            e.printStackTrace();
        }
        finally
        {
            if (output != null)
            {
                try
                {
                    output.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getProperty(String key)
    {
        return this.properties_motd.getProperty(key);
    }

    /**
     *
     * @param selection can be one of:
     *                  DimensionTeleportCommand
     *                  mod_bugs
     * @return
     */
    private Properties loadDefaults(String selection)
    {
        Properties props = new Properties();

        switch (selection)
        {
            case "DimensionTeleportCommand":
                properties_motd.setProperty("welcome.message", welcome_message_example);
                properties_motd.setProperty("DimensionTeleportCommand.text", motd_text_example + " 1");
                properties_motd.setProperty("DimensionTeleportCommand.text", motd_text_example + " 2");

                break;
            case "mod_bugs":
                //TODO
                break;
        }

        return props;
    }

}
