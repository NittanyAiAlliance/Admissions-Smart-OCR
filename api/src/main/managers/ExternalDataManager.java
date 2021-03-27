package main.managers;

import main.data.DatabaseInteraction;

public class ExternalDataManager {
    private DatabaseInteraction database;

    /**
     * Default constructor ~ create new database interaction instance for the manager
     */
    public ExternalDataManager(){
        this.database = new DatabaseInteraction();
    }

    /**
     * Make an API request to
     */
    public void requestAvailableTranscripts(){

    }
}
