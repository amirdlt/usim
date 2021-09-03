package com.usim.ulib.notmine;


import java.util.ArrayList;
import java.util.List;

public class Course {
    public final static String PHD = "PHD";
    public final static String BS = "BS";
    public final static String MS = "MS";

    private static volatile int phdCourseIndex = 1000;
    private static volatile int bsCourseIndex = 2000;
    private static volatile int msCourseIndex = 3000;

    private final int courseId;
    private String name;
    private String courseLevel;
    private Course preRequisite;
    private int units;

    public Course(String courseLevel, String name, int units) {
        if (!isValidName(name))
            throw new RuntimeException("This name is not valid!");
        if (units > 4 || units < 1)
            throw new RuntimeException("Units should be 1 up to 4!");
        this.courseLevel = courseLevel;
        if (courseLevel.equals(PHD)) {
            courseId = phdCourseIndex++;
        } else if (courseLevel.equals(BS)) {
            courseId = bsCourseIndex++;
        } else if (courseLevel.equals(MS)) {
            courseId = msCourseIndex++;
        } else {
            courseId = -1;
        }
        this.name = name;
        this.units = units;
        this.preRequisite = null;
    }

    public Course(String courseLevel, String name) {
        this(courseLevel, name, 3);
    }

    public Course(String courseLevel) {
        this(courseLevel, null, 3);
    }

    public static boolean isValidName(String name) {
        if (name == null)
            return true;
        if (name.length() < 10)
            return false;
        boolean hasDigit = false;
        for (char ch : name.toCharArray()) {
            if (!Character.isAlphabetic(ch) && !Character.isDigit(ch) && !Character.isWhitespace(ch))
                return false;
            if (Character.isDigit(ch))
                hasDigit = true;
            if (hasDigit && Character.isAlphabetic(ch))
                return false;
        }
        return true;
    }

    public static boolean haveCommonPreRequisite(Course course1, Course course2) {
        List<Course> course1PreRequisite = new ArrayList<>();
        List<Course> course2PreRequisite = new ArrayList<>();
        while ((course1 = course1.preRequisite) != null)
            course1PreRequisite.add(course1);
        while ((course2 = course2.preRequisite) != null)
            course2PreRequisite.add(course2);
        for (Course c1 : course1PreRequisite)
            for (Course c2 : course2PreRequisite)
                if (c1.courseId == c2.courseId)
                    return true;
        return false;
    }

    public Course createAdvancedCourse() {
        Course result = new Course(courseLevel, "Advanced " + name, units);
        result.setPreRequisite(this);
        return result;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getCourseLevel() {
        return courseLevel;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        if (units > 4 || units < 1) {
            System.err.println("Units should be 1 up to 4!");
            return;
        }
        this.units = units;
    }

    public Course getPreRequisite() {
        return preRequisite;
    }

    public void setPreRequisite(Course preRequisite) {
        this.preRequisite = preRequisite;
    }

    public void setName(String name) {
        if (!isValidName(name)) {
            System.err.println("This name is not valid!");
            return;
        }
        this.name = name;
    }

    public void setCourseLevel(String courseLevel) {
        this.courseLevel = courseLevel;
    }
}
