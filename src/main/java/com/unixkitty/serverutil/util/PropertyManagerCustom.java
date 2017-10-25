package com.unixkitty.serverutil.util;

import com.unixkitty.serverutil.ServerUtilMod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@SideOnly(Side.SERVER)
public class PropertyManagerCustom
{
    private Properties properties;
    private final File file;
    private final String comment;

    public PropertyManagerCustom(File propertiesFile, String fileComment)
    {
        this.file = propertiesFile;
        this.comment = fileComment;

        loadFile();
    }

    public void loadFile()
    {
        this.properties = new Properties();

        if (this.file.exists())
        {
            FileInputStream fileinputstream = null;

            try
            {
                fileinputstream = new FileInputStream(this.file);
                this.properties.load(fileinputstream);
            }
            catch (Exception exception)
            {
                ServerUtilMod.log.warn("Failed to load {}", this.file, exception);
                this.generateNewProperties();
            }
            finally
            {
                if (fileinputstream != null)
                {
                    try
                    {
                        fileinputstream.close();
                    }
                    catch (IOException var11)
                    {
                        ;
                    }
                }
            }
        }
        else
        {
            ServerUtilMod.log.warn("{} does not exist", this.file);
            this.generateNewProperties();
        }
    }

    /**
     * Generates a new properties file.
     */
    public void generateNewProperties()
    {
        ServerUtilMod.log.info("Generating new properties file");
        this.saveProperties();
    }

    /**
     * Writes the properties to the properties file.
     */
    public void saveProperties()
    {
        FileOutputStream fileoutputstream = null;

        try
        {
            fileoutputstream = new FileOutputStream(this.file);
            this.properties.store(fileoutputstream, comment);
        }
        catch (Exception exception)
        {
            ServerUtilMod.log.warn("Failed to save {}", this.file, exception);
            this.generateNewProperties();
        }
        finally
        {
            if (fileoutputstream != null)
            {
                try
                {
                    fileoutputstream.close();
                }
                catch (IOException var10)
                {
                    ;
                }
            }
        }
    }

    /**
     * Returns this PropertyManager's file object used for property saving.
     */
    public File getPropertiesFile()
    {
        return this.file;
    }

    /**
     * Returns a string property. If the property doesn't exist the default is returned.
     */
    public String getStringProperty(String key, String defaultValue)
    {
        if (!this.properties.containsKey(key))
        {
            this.properties.setProperty(key, defaultValue);
            this.saveProperties();
            this.saveProperties();
        }

        return this.properties.getProperty(key, defaultValue);
    }

    /**
     * Gets an integer property. If it does not exist, set it to the specified value.
     */
    public int getIntProperty(String key, int defaultValue)
    {
        try
        {
            return Integer.parseInt(this.getStringProperty(key, "" + defaultValue));
        }
        catch (Exception var4)
        {
            this.properties.setProperty(key, "" + defaultValue);
            this.saveProperties();
            return defaultValue;
        }
    }

    public long getLongProperty(String key, long defaultValue)
    {
        try
        {
            return Long.parseLong(this.getStringProperty(key, "" + defaultValue));
        }
        catch (Exception var5)
        {
            this.properties.setProperty(key, "" + defaultValue);
            this.saveProperties();
            return defaultValue;
        }
    }

    /**
     * Gets a boolean property. If it does not exist, set it to the specified value.
     */
    public boolean getBooleanProperty(String key, boolean defaultValue)
    {
        try
        {
            return Boolean.parseBoolean(this.getStringProperty(key, "" + defaultValue));
        }
        catch (Exception var4)
        {
            this.properties.setProperty(key, "" + defaultValue);
            this.saveProperties();
            return defaultValue;
        }
    }

    /**
     * Saves an Object with the given property name.
     */
    public void setProperty(String key, Object value)
    {
        this.properties.setProperty(key, "" + value);
    }

    public boolean hasProperty(String key)
    {
        return this.properties.containsKey(key);
    }

    public void removeProperty(String key)
    {
        this.properties.remove(key);
    }
}