package org.sbe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class ProjectMain
{
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(ProjectMain.class.getName());

    /**
     * Method that runs the main functionality of the project.
     *
     * @param   args
     *          Command line arguments.
     */
    public static void main(String[] args)
    {
        // Check if setup is necessary at startup
        ConfigReader reader = ConfigReader.getInstance();
        if (Boolean.parseBoolean(reader.getProperty("setup.initiate")))
        {
            LOGGER.info("Publication generation is enabled.");
            try
            {
                // Runs a python script tasked with generating data for publications and subscriptions
                URI uriPython = ProjectMain.class.getResource("/main.py").toURI();
                Path pythonScript = Paths.get(uriPython);
                URI uriConfig = ProjectMain.class.getResource("/config.ini").toURI();
                Path configFilePath = Paths.get(uriConfig);
                URI uriResource = ProjectMain.class.getResource("/").toURI();
                Path resourceFolder = Paths.get(uriResource);
                ProcessBuilder processBuilder = new ProcessBuilder("python",
                                                                   pythonScript.toString(),
                                                                   configFilePath.toString(),
                                                                   resourceFolder.toString());
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                // View log outputs of the python script that is running
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader1.readLine()) != null)
                {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                if (exitCode == 0)
                {
                    LOGGER.info("Python script executed successfully.");
                    try
                    {
                        // We need to copy the generated publication and subscription files
                        // from the the source folder to our resources folder.
                        String sourceDirectoryPath = "target/classes/";
                        String destinationDirectoryPath = "src/main/resources";

                        copyFiles(sourceDirectoryPath, destinationDirectoryPath);

                        LOGGER.info("Files copied successfully.");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    LOGGER.severe("Python script execution failed.");
                }
            }
            catch (Exception e)
            {
                LOGGER.severe("Error running python script.");
            }
        }
    }

    /**
     * Method that copies files with the .json extension from a source folder
     * to a destination folder.
     *
     * @param   sourceDirectoryPath
     *          The source folder containing the json files.
     * @param   destinationDirectoryPath
     *          The destination folder where we want to copy the json files.
     *
     * @throws IOException
     */
    private static void copyFiles(String sourceDirectoryPath, String destinationDirectoryPath)
    throws IOException
    {
        File sourceDirectory = new File(sourceDirectoryPath);

        File[] files = sourceDirectory.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                String destinationFilePath = destinationDirectoryPath + File.separator + file.getName();

                Path sourcePath = file.toPath();
                if (!sourcePath.toString().endsWith(".json"))
                {
                    continue;
                }

                Path destinationPath = new File(destinationFilePath).toPath();
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        else
        {
            LOGGER.severe("There are no .json files to copy.");
        }
    }
}