package org.sbe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * A Singleton class that reads properties for the project setup.
 */
public class ConfigReader
{
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(ConfigReader.class.getName());

    /** The name of the configuration file. */
    private static final String RESOURCE_FILE = "project.properties";

    /** Reference to the only instance of this class. */
    private static ConfigReader instance = null;

    /** The properties that are read from the configuration properties. */
    private final Properties properties;

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Initializes the properties object and loads the configuration file.
     */
    private ConfigReader()
    {
        this.properties = new Properties();
        try
        {
            InputStream propertiesInput = ConfigReader.class.getClassLoader().getResourceAsStream(RESOURCE_FILE);
            this.properties.load(propertiesInput);
        }
        catch (IOException ex)
        {
            LOGGER.severe("Error reading project properties.");
        }
    }

    /**
     * Retrieves the singleton instance of the ConfigReader class.
     * if the instance does not exist, it is created.
     *
     * @return  The singleton instance of ConfigReader.
     */
    public static ConfigReader getInstance()
    {
        if (instance == null)
        {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Get the properties obtained from the configuration file.
     *
     * @return  The properties from the configuration file.
     */
    public Properties getProperties()
    {
        return properties;
    }

    /**
     * Method that retrieves a specific property using a given string.
     *
     * @param   key
     *          The property that will be retrieved.
     *
     * @return  The value of the property requested.
     */
    public String getProperty(String key)
    {
        return properties.getProperty(key);
    }
}
