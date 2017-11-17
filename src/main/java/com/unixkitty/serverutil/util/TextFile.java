package com.unixkitty.serverutil.util;

import com.google.common.io.Files;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TextFile
{
    private final Charset encoding;
    private final File file;
    private final List<String> defaultContents;
    private final Logger logger;

    public TextFile(File file, Charset encoding, List<String> defaultContents, Logger logger)
    {
        this.file = file;
        this.encoding = encoding;
        this.defaultContents = defaultContents;
        this.logger = logger;
    }

    public List<String> readFile()
    {
        if (this.file.exists())
        {
            try (BufferedReader reader = Files.newReader(file, encoding))
            {
                String line;
                List<String> text = new ArrayList<>();

                while ((line = reader.readLine()) != null)
                {
                    text.add(line);
                }

                return text;
            }
            catch (IOException e)
            {
                logger.warn("Failed to load {}", file, e);
                this.newFile(defaultContents);
                return defaultContents;
            }
        }
        else
        {
            logger.info("{} does not exist", file);
            this.newFile(defaultContents);
            return defaultContents;
        }
    }

    public void newFile(List<String> text)
    {
        logger.info("Creating new file {}", file);
        this.saveFile(text);
    }

    public void saveFile(List<String> text)
    {
        try (BufferedWriter writer = Files.newWriter(file, encoding))
        {
            for (String line : text)
            {
                writer.write(line);
                writer.newLine();
            }
        }
        catch (IOException e)
        {
            logger.warn("Failed to save {}", file, e);
            this.newFile(text);
        }
    }

    public File getFile()
    {
        return this.file;
    }

    public Charset getEncoding()
    {
        return this.encoding;
    }

    public List<String> getDefaultContents()
    {
        return this.defaultContents;
    }
}
