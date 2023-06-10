package org.sbe.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sbe.ConfigReader;
import org.sbe.ProjectMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class DataManager
{
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(DataManager.class.getName());

    private static DataManager instance = null;
    private List<Publication> publications;
    private List<Subscription> subscriptions;

    private DataManager()
    {
        this.publications = new ArrayList<>();
        this.subscriptions = new ArrayList<>();
        getData();
    }

    public static DataManager getInstance()
    {
        if (instance == null)
        {
            instance = new DataManager();
        }
        return instance;
    }

    public List<Publication> getPublications()
    {
        return publications;
    }

    public void setPublications(List<Publication> publications)
    {
        this.publications = publications;
    }

    public List<Subscription> getSubscriptions()
    {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions)
    {
        this.subscriptions = subscriptions;
    }

    public void addPublication(Publication publication)
    {
        this.publications.add(publication);
    }

    public void addSubscription(Subscription subscription)
    {
        this.subscriptions.add(subscription);
    }

    private void getData()
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
                        LOGGER.severe("Files could not be copied.");
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
        else
        {
            LOGGER.info("Publication generation is disabled.");
        }

        try
        {
            // Read the publication and subscription data and create JSONObjects for each one.
            String publicationsPath = Paths.get(ProjectMain.class.getResource("/publications.json").toURI()).toString();
            String subscriptionsPath = Paths.get(ProjectMain.class.getResource("/subscriptions.json").toURI()).toString();
            String publicationsContent = new String(Files.readAllBytes(Paths.get(publicationsPath)));
            String subscriptionsContent = new String(Files.readAllBytes(Paths.get(subscriptionsPath)));

            JSONObject publicationsJson = new JSONObject(publicationsContent);
            JSONObject subscriptionsJson = new JSONObject(subscriptionsContent);

            JSONArray publicationsArray = publicationsJson.getJSONArray("data");
            for (int i = 0; i < publicationsArray.length(); i++)
            {
                JSONObject dataObject = publicationsArray.getJSONObject(i);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = null;
                try
                {
                    date = dateFormat.parse(dataObject.getString("date"));
                }
                catch (ParseException ex)
                {
                    LOGGER.severe("Invalid date format in publication data.");
                    continue;
                }
                float rain = dataObject.getFloat("rain");
                int temp = dataObject.getInt("temp");
                String city = dataObject.getString("city");
                int stationID = dataObject.getInt("station_id");
                int wind = dataObject.getInt("wind");
                String direction = dataObject.getString("direction");

                if (date == null)
                {
                    continue;
                }

                Publication publication = new Publication(stationID, city, temp, rain, wind, direction, date);
                this.addPublication(publication);
            }

            JSONArray subscriptionsArray = subscriptionsJson.getJSONArray("data");
            for (int i = 0; i < subscriptionsArray.length(); i++)
            {
                JSONArray dataObjectArray = subscriptionsArray.getJSONArray(i);
                List<Constraint> constraints = new ArrayList<>();
                for (int j = 0; j < dataObjectArray.length(); j++)
                {
                    JSONObject constraintObject = dataObjectArray.getJSONObject(j);
                    System.out.println(constraintObject.toString());
                    String factor = constraintObject.getString("factor");
                    String operator = constraintObject.getString("operator");
                    String requiredValue = constraintObject.getString("required_value");

                    Constraint constraint = new Constraint(factor, operator, requiredValue);
                    constraints.add(constraint);
                }

                if (constraints.size() == 0)
                {
                    continue;
                }

                Subscription subscription = new Subscription(constraints);
                addSubscription(subscription);
            }
        }
        catch (URISyntaxException e)
        {
            LOGGER.severe("Invalid URI paths for publication and subscription files.");
        }
        catch (IOException ex)
        {
            LOGGER.severe("Publication and subscription files not found.");
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
