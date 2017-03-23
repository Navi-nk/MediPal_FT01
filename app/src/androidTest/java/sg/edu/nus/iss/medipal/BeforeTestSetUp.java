package sg.edu.nus.iss.medipal;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import sg.edu.nus.iss.medipal.dao.HealthBioDAO;
import sg.edu.nus.iss.medipal.dao.PersonalBioDAO;
import sg.edu.nus.iss.medipal.manager.DataBaseManager;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Divahar on 3/21/2017.
 */

@RunWith(AndroidJUnit4.class)
public class BeforeTestSetUp {

    private static DataBaseManager dbManager;
    private static Context context;
    public static HealthBioDAO healthBioDAO;
    public static PersonalBioDAO personalBioDAO;

    @BeforeClass
    public static void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase("medipalFT01DB");
        dbManager = new DataBaseManager(context);
        healthBioDAO = new HealthBioDAO(context);
        personalBioDAO = new PersonalBioDAO(context);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        healthBioDAO.close();
        personalBioDAO.close();
    }

    @Test
    public void testSetUp() {
        assertNotNull(healthBioDAO);
        assertNotNull(personalBioDAO);
    }

}