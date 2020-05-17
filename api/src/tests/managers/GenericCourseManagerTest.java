package tests.managers;

import main.managers.GenericCourseManager;
import main.types.GenericCourse;
import main.types.GenericSubjectArea;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

/**
 * Test suite for the generic course data interactions
 * @author Joel Seidel
 */
public class GenericCourseManagerTest {
    @Test
    public void testGetAllGenericCourseOptions() {
        GenericCourseManager genericCourseManager = new GenericCourseManager();
        List<GenericSubjectArea> allCourses = genericCourseManager.getAllGenericCourseOptions();
        //There should be 11 generic subject areas in the test database
        assertEquals(allCourses.size(), 11);
        GenericSubjectArea genericSubjectArea = allCourses.get(0);
        //Assertions on the first generic subject area in the test database
        assertEquals(genericSubjectArea.getName(), "Business");
        assertEquals(genericSubjectArea.getCode(), "BS");
        //The first generic subject area should have 19 associated courses
        assertEquals(genericSubjectArea.getCourses().size(), 19);
        GenericCourse genericCourse = genericSubjectArea.getCourses().get(0);
        //Assertions on the first generic course in the test database
        assertEquals(genericCourse.getCode(), "ACC1");
        assertEquals(genericCourse.getName(), "ACCOUNTING 1");
    }
    @Test
    public void testConvertCourseOptionsToJson() {
        GenericCourseManager genericCourseManager = new GenericCourseManager();
        List<GenericSubjectArea> allCourses = genericCourseManager.getAllGenericCourseOptions();
        JSONObject allCoursesJson = genericCourseManager.convertCourseOptionsToJson(allCourses);
        //There should be an array of subject areas
        assertTrue(allCoursesJson.has("subjects"));
        JSONArray allSubjectsArr = allCoursesJson.getJSONArray("subjects");
        //The subject area array should be larger than 0
        assertTrue(allSubjectsArr.length() > 0);
        for(int i = 0; i < allSubjectsArr.length(); i++) {
            JSONObject thisSubject = allSubjectsArr.getJSONObject(i);
            assertTrue(thisSubject.has("code"));
            assertTrue(thisSubject.has("name"));
            assertTrue(thisSubject.has("courses"));
        }
    }
}
