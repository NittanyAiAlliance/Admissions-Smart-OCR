package main.managers;

import main.data.DatabaseInteraction;
import main.types.GenericCourse;
import main.types.GenericSubjectArea;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenericCourseManager {
    private DatabaseInteraction database;

    public GenericCourseManager(){
        this.database = new DatabaseInteraction();
    }

    /**
     * Get all of the generic course options ~ organized by generic subject areas
     * @return list of all generic subject areas, containing all of the generic course options
     */
    public List<GenericSubjectArea> getAllGenericCourseOptions(){
        //TODO: combine these queries into a join
        List<GenericSubjectArea> genericSubjectAreas = new ArrayList<>();
        String getSubjectsSql = "SELECT * FROM SUBJECT_AREAS";
        PreparedStatement getSubjectsStmt = database.prepareStatement(getSubjectsSql);
        try {
            //Get the generic subject areas
            ResultSet getCoursesResults = database.query(getSubjectsStmt);
            while(getCoursesResults.next()){
                //Parse this generic subject area
                String subjectCode = getCoursesResults.getString("CODE");
                String subjectName = getCoursesResults.getString("NAME");
                GenericSubjectArea subjectArea = new GenericSubjectArea(subjectCode, subjectName);
                //Get the courses for the subject area
                String getCoursesSql = "SELECT COURSE_CODE, COURSE_NAME FROM GENERIC_COURSES WHERE SUBJECT_CODE = ?";
                PreparedStatement getCoursesStmt = database.prepareStatement(getCoursesSql);
                try {
                    getCoursesStmt.setString(1, subjectCode);
                    ResultSet courseResults = database.query(getCoursesStmt);
                    while(courseResults.next()){
                        //Parse the generic course options for this generic subject area
                        String courseName = courseResults.getString("COURSE_NAME");
                        String courseCode = courseResults.getString("COURSE_CODE");
                        subjectArea.addCourse(new GenericCourse(courseCode, courseName));
                    }
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                genericSubjectAreas.add(subjectArea);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return genericSubjectAreas;
    }

    /**
     * Convert a list of generic subject areas into a JSONObject containing a JSONArray of the subject areas and courses
     * @param subjectAreas list of subject areas to convert to JSON
     * @return JSON Object representation of the generic subject area list
     */
    public JSONObject convertCourseOptionsToJson(List<GenericSubjectArea> subjectAreas){
        //TODO: move convert to json functionality into the type classes
        JSONArray subjectAreaArray = new JSONArray();
        for (GenericSubjectArea thisSubject : subjectAreas) {
            JSONObject thisSubjectObj = new JSONObject();
            thisSubjectObj.put("code", thisSubject.getCode());
            thisSubjectObj.put("name", thisSubject.getName());
            JSONArray coursesArray = new JSONArray();
            for (int j = 0; j < thisSubject.getCourses().size(); j++) {
                GenericCourse thisCourse = thisSubject.getCourses().get(j);
                JSONObject thisCourseObj = new JSONObject();
                thisCourseObj.put("code", thisCourse.getCode());
                thisCourseObj.put("name", thisCourse.getName());
                coursesArray.put(thisCourseObj);
            }
            thisSubjectObj.put("courses", coursesArray);
            subjectAreaArray.put(thisSubjectObj);
        }
        return new JSONObject().put("subjects", subjectAreaArray);
    }
}
