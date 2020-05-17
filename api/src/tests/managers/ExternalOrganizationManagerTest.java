package tests.managers;

import main.managers.ExternalOrganizationManager;
import main.types.ExternalOrganization;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tests for the external organization data interactions
 */
public class ExternalOrganizationManagerTest {
    //Test ceeb code ~ will bring up Boyertown Area Senior High School (rep the Bears)
    int test_ceeb = 390390;

    @Test
    public void testGetByCeeb(){
        ExternalOrganizationManager externalOrganizationManager = new ExternalOrganizationManager();
        ExternalOrganization gotByCeeb = externalOrganizationManager.getByCeeb(test_ceeb);
        //Assert on the database properties
        assertEquals(gotByCeeb.getName(), "Boyertown Area Senior Hs");
        assertEquals(gotByCeeb.getAtpCode(), 390390);
        assertEquals(gotByCeeb.getAddressLine1(), "120 N Monroe St");
        assertEquals(gotByCeeb.getAddressLine2(), "");
        assertEquals(gotByCeeb.getCity(), "Boyertown");
        assertEquals(gotByCeeb.getState(), "PA");
        assertEquals(gotByCeeb.getZip(), "19512");
        assertEquals(gotByCeeb.getCountry(), "USA");
    }
}
