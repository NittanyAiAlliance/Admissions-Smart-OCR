package main.managers;

import main.data.DatabaseInteraction;
import main.types.ExternalOrganization;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract data interactions with the external organization data set
 * @author Joel Seidel
 */
public class ExternalOrganizationManager {
    private DatabaseInteraction database;

    /**
     * Default constructor to initiaize the database connection
     */
    public ExternalOrganizationManager(){
        this.database = new DatabaseInteraction();
    }

    /**
     * Get an external organization information from data by its CEEB (ATP) code
     * @param ceeb the CEEB (ATP) code to look for
     * @return the matching external organization
     */
    public ExternalOrganization getByCeeb(int ceeb){
        String getByCeebSql = "SELECT * FROM EXTERNAL_ORGS WHERE ATP_CODE = ?";
        PreparedStatement getByCeebStmt = database.prepareStatement(getByCeebSql);
        try {
            getByCeebStmt.setInt(1, ceeb);
            ResultSet extOrgResult = database.query(getByCeebStmt);
            if(extOrgResult.next()){
                String name = extOrgResult.getString("NAME");
                String address[] = new String[] { extOrgResult.getString("ADDRESS_LINE_1"), extOrgResult.getString("ADDRESS_LINE_2") };
                String city = extOrgResult.getString("CITY");
                String state = extOrgResult.getString("STATE");
                String zip = extOrgResult.getString("ZIP");
                String country = extOrgResult.getString("COUNTRY");
                ExternalOrganization thisExtOrg = new ExternalOrganization(ceeb, name, address, city, state, zip, country);
                return thisExtOrg;
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return null;
    }
}
