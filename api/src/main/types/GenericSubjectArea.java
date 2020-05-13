package main.types;

import java.util.ArrayList;
import java.util.List;

public class GenericSubjectArea {
    private String code, name;
    private List<GenericCourse> courses;

    /**
     * Default constructor
     * @param code code for this generic subject area
     * @param name name of the generic subject area
     * @param courses list of generic courses
     */
    public GenericSubjectArea(String code, String name, List<GenericCourse> courses){
        this.code = code;
        this.name = name;
        this.courses = courses;
    }

    public GenericSubjectArea(String code, String name){
        this.code = code;
        this.name = name;
        this.courses = new ArrayList<>();
    }

    /**
     * Add a course to this subject area object
     * @param course course object to add to subject area
     */
    public void addCourse(GenericCourse course){
        this.courses.add(course);
    }

    /**
     * Getter for generic subject area code
     * @return generic subject area
     */
    public String getCode() {
        return code;
    }

    /**
     * Getter for generic subject area name
     * @return generic subject area name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the generic subject area course list
     * @return list of generic courses within this generic subject area
     */
    public List<GenericCourse> getCourses() {
        return courses;
    }
}