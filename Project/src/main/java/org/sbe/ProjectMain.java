package org.sbe;

import org.sbe.data.DataManager;
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
        DataManager dm = DataManager.getInstance();
    }
}