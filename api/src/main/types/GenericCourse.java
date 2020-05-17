package main.types;

public class GenericCourse {
    private String code, name;
    public GenericCourse(String code, String name){
        this.code = code;
        this.name = name;
    }

    /**
     * Getter for generic course code
     * @return generic course code
     */
    public String getCode(){
        return this.code;
    }

    /**
     * Getter for generic course name
     * @return generic course name
     */
    public String getName(){
        return this.name;
    }
}
