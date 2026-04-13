package com.ujs.trainingprogram.tp.dto.req.version;

import lombok.Data;

import java.util.List;

@Data
public class VersionSaveChangesReqDTO {

    private String trainingProgramId;

    private String versionName;

    private String changeDescription;

    private List<CourseChangeItem> addedCourses;

    private List<CourseChangeItem> updatedCourses;

    private List<String> deletedCourseIds;

    @Data
    public static class CourseChangeItem {
        private String id;
        private String courseId;
        private String collegeId;
        private String majorId;
        private Float totalCredits;
        private Float totalHours;
        private Float totalWeeks;
        private Integer hoursUnit;
        private Integer hourTeach;
        private Integer hourPractice;
        private Integer hourOperation;
        private Integer hourOutside;
        private Integer hourWeek;
        private Integer requiredElective;
        private Integer term;
        private String remark;
        private Integer courseNature;
        private String courseName;
    }
}
