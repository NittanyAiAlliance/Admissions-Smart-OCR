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

    public List<GenericSubjectArea> getAllGenericCourseOptions(){
        List<GenericSubjectArea> genericSubjectAreas = new ArrayList<>();
        String getSubjectsSql = "SELECT * FROM SUBJECT_AREAS";
        PreparedStatement getSubjectsStmt = database.prepareStatement(getSubjectsSql);
        try {
            ResultSet getCoursesResults = database.query(getSubjectsStmt);
            while(getCoursesResults.next()){
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

    public JSONObject convertCourseOptionsToJson(List<GenericSubjectArea> subjectAreas){
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
