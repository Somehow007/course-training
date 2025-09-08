package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseCategoryEnum;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseTypeEnum;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程业务控制层
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    private final MajorService majorService;

    private final CollegeService collegeService;

    @GetMapping("/course/")
    public ResultData getAllCourse(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size,
                                   @RequestParam(value = "course_id", defaultValue = "-1") Integer courseId,
                                   @RequestParam(value = "course_code", defaultValue = "") String courseCode,
                                   @RequestParam(value = "course_type", defaultValue = "-1") Integer courseType,
                                   @RequestParam(value = "course_category", defaultValue = "-1") Integer courseCategory,
                                   @RequestParam(value = "course_name", defaultValue = "") String courseName,
                                   @RequestParam(value = "college_name", defaultValue = "") String collegeName,
                                   @RequestParam(value = "major_name", defaultValue = "") String majorName,
                                   @RequestParam(value = "year", defaultValue = "") Integer year) {
        log.info("请求参数：page={}, size={}, course_id={}, courseCode={}, courseName={}, course_type={}, course_category={}, major_id={}, college_id={}, year={}", page, size, courseId, courseCode, courseName, courseType, courseCategory, majorName, collegeName, year);
        QueryWrapper<CourseDO> wrapper = new QueryWrapper<>();
        if (courseId != -1) {
            wrapper.eq("course_id", courseId);
        } else {
            wrapper.eq(!StringUtils.isEmpty(courseCode), "course_code", courseCode);
            wrapper.eq(!StringUtils.isEmpty(courseName), "course_name", courseName);
            wrapper.eq(courseType != -1, "course_type", courseType);
            wrapper.eq(courseCategory != -1, "course_category", courseCategory);
            if (!StringUtils.isEmpty(majorName)) {
                List<MajorDO> majorDOS = majorService.getMajorLikeByName(majorName);
                if (!CollectionUtils.isEmpty(majorDOS)) {
                    wrapper.in("major_id", majorDOS.stream().map(MajorDO::getMajorId).collect(Collectors.toList()));
                }
            }
            if (!StringUtils.isEmpty(collegeName)) {
                List<CollegeDO> collegeDOS = collegeService.getCollegeLikeByName(collegeName);
                if (!CollectionUtils.isEmpty(collegeDOS)) {
                    wrapper.in("college_id",
                            collegeDOS.stream().map(CollegeDO::getCollegeId).collect(Collectors.toList()));
                }
            }
        }
        wrapper.like(StringUtils.isEmpty(year + ""), "year", year);
        return courseService.selectWithWrapper(page, size, wrapper);
    }

    /**
     * 系统管理员可添加任意学院的课程
     * @return
     */
    @PostMapping("/course/mainAdmin/add")
    public ResultMessage addCourse(
            @RequestParam(value = "course_code", defaultValue = "") String courseCode,
            @RequestParam(value = "course_type", defaultValue = "-1") Integer courseType,
            @RequestParam(value = "course_category", defaultValue = "-1") Integer courseCategory,
            @RequestParam(value = "course_name", defaultValue = "") String courseName,
            @RequestParam(value = "college_name", defaultValue = "") String collegeName,
            @RequestParam(value = "major_name", defaultValue = "") String majorName,
            @RequestParam(value = "total_credits", defaultValue = "-1.0") Float totalCredits,
            @RequestParam(value = "total_hours", defaultValue = "-1.0") Float totalHours,
            @RequestParam(value = "total_weeks", defaultValue = "-1.0") Float totalWeeks,
            @RequestParam(value = "hour_teach", defaultValue = "-1") Integer hourTeach,
            @RequestParam(value = "hour_practice", defaultValue = "-1") Integer hourPractice,
            @RequestParam(value = "hour_option", defaultValue = "-1") Integer hourOption,
            @RequestParam(value = "hour_outside", defaultValue = "-1") Integer hourOutside,
            @RequestParam(value = "hour_week", defaultValue = "-1") Integer hourWeek,
            @RequestParam(value = "required_elective", defaultValue = "0") byte requiredElective,
            @RequestParam(value = "term", defaultValue = "-1") Integer term) {
        if (StringUtils.isAnyEmpty(courseCode, courseName, majorName)) return ResultMessage.PARAM_MISS;
        CourseDO courseDO = new CourseDO();
        List<CollegeDO> collegeDOS = collegeService.getCollegeLikeByName(collegeName);
        List<MajorDO> majorDOS = majorService.getMajorLikeByName(majorName);
        if (collegeDOS.size() == 0 || majorDOS.size() == 0) return ResultMessage.PARAM_MISS;
        courseDO.setCollegeId(collegeDOS.get(0).getCollegeId());
        courseDO.setMajorId(majorDOS.get(0).getMajorId());

        courseDO.setCourseCode(courseCode);
        courseDO.setCourseName(courseName);
        courseDO.setTotalCredits(totalCredits < 0 ? null : totalCredits);
        courseDO.setHourWeek(hourWeek < 0 ? null : hourWeek);
        courseDO.setHourTeach(hourTeach < 0 ? null : hourTeach);
        courseDO.setHourPractice(hourPractice < 0 ? null : hourPractice);
        courseDO.setHourOperation(hourOption < 0 ? null : hourOption);
        courseDO.setHourOutside(hourOutside < 0 ? null : hourOutside);
        courseDO.setCourseType(CourseTypeEnum.getInstance(courseType));
        courseDO.setCourseCategory(CourseCategoryEnum.getInstance(courseCategory));
        courseDO.setTerm(term);
        boolean flag=courseService.save(courseDO);
        if (!flag) {
            return ResultMessage.UPDATE_ERROR;
        }
        collegeService.modifyCourseNum(collegeDOS.get(0).getCollegeId(),1);
        majorService.modifyCourseNum(majorDOS.get(0).getMajorId(),1);

        return ResultMessage.UPDATE_SUCCESS;
    }

    /**
     * 修改课程信息
     */
    @PutMapping("/course/mainAdmin/update")
    public ResultMessage updateCourse(
            @RequestParam(value = "course_id", defaultValue = "-1") Integer courseId,
            @RequestParam(value = "course_code", defaultValue = "") String courseCode,
            @RequestParam(value = "course_type", defaultValue = "-1") Integer courseType,
            @RequestParam(value = "course_category", defaultValue = "-1") Integer courseCategory,
            @RequestParam(value = "course_name", defaultValue = "") String courseName,
            @RequestParam(value = "college_name", defaultValue = "") String collegeName,
            @RequestParam(value = "major_name", defaultValue = "") String majorName,
            @RequestParam(value = "total_credits", defaultValue = "-1.0") Float totalCredits,
            @RequestParam(value = "total_hours", defaultValue = "-1.0") Float totalHours,
            @RequestParam(value = "total_weeks", defaultValue = "-1.0") Float totalWeeks,
            @RequestParam(value = "hour_teach", defaultValue = "-1") Integer hourTeach,
            @RequestParam(value = "hour_practice", defaultValue = "-1") Integer hourPractice,
            @RequestParam(value = "hour_option", defaultValue = "-1") Integer hourOption,
            @RequestParam(value = "hour_outside", defaultValue = "-1") Integer hourOutside,
            @RequestParam(value = "hour_week", defaultValue = "-1") Integer hourWeek,
            @RequestParam(value = "required_elective", defaultValue = "0") byte requiredElective,
            @RequestParam(value = "term", defaultValue = "-1") Integer term) {

        if (courseId.equals(-1)) {
            return ResultMessage.PARAM_MISS;
        }
        CourseDO courseDO = courseService.getById(courseId);
        if (courseDO == null) {
            return ResultMessage.PARAM_MISS;
        }

        courseDO.setCourseCode(courseCode);
        courseDO.setCourseName(courseName);
        courseDO.setTotalCredits(totalCredits < 0 ? null : totalCredits);
        courseDO.setHourWeek(hourWeek < 0 ? null : hourWeek);
        courseDO.setHourTeach(hourTeach < 0 ? null : hourTeach);
        courseDO.setHourPractice(hourPractice < 0 ? null : hourPractice);
        courseDO.setHourOperation(hourOption < 0 ? null : hourOption);
        courseDO.setHourOutside(hourOutside < 0 ? null : hourOutside);
        courseDO.setCourseType(CourseTypeEnum.getInstance(courseType));
        courseDO.setCourseCategory(CourseCategoryEnum.getInstance(courseCategory));
        courseDO.setTerm(term);
        courseDO.setRequiredElective(requiredElective < 0 ? null : requiredElective);
        courseDO.setTotalWeeks(totalWeeks < 0 ? null : totalWeeks + "周");
        String collegeId = collegeService.getCollegeLikeByName(collegeName).get(0).getCollegeId();
        String majorId = majorService.getMajorLikeByName(majorName).get(0).getMajorId();
        courseDO.setCollegeId(collegeId);
        courseDO.setMajorId(majorId);
        courseDO.setTotalHours(totalHours < 0 ? null : totalHours);
        return courseService.saveOrUpdate(courseDO) ? ResultMessage.UPDATE_SUCCESS : ResultMessage.UPDATE_ERROR;
    }

    /**
     * 删除课程
     */
    private boolean delete(Integer courseId) {
        CourseDO courseDO = courseService.getById(courseId);
        if(courseDO ==null)
            return false;
        //修改开课学院与授课专业需求量
        CollegeDO collegeDO = collegeService.getById(courseDO.getCollegeId());
        MajorDO majorDO = majorService.getById(courseDO.getMajorId());
        collegeDO.setCourseNum(collegeDO.getCourseNum() - 1);
        majorDO.setCourseNum(majorDO.getCourseNum() - 1);
        collegeService.saveOrUpdate(collegeDO);
        majorService.saveOrUpdate(majorDO);
        return courseService.removeById(courseId);
    }

    /**
     * 删除单个课程
     */
    @DeleteMapping("/course/mainAdmin/delete")
    public ResultMessage deleteCourse(@RequestParam(value = "course_id") Integer courseId) {
        if (courseId == null) {
            return ResultMessage.PARAM_MISS;
        }
        return delete(courseId) ? ResultMessage.DELETE_SUCCESS : ResultMessage.DELETE_ERROR;
    }

    /**
     * 批量删除课程
     */
    @DeleteMapping("/course/mainAdmin/deleteAll")
    public ResultMessage deleteAllCourse(@RequestParam(value = "course_ids",defaultValue = "") String courseIds) {
        int num=0;
        //获取参数
        if (courseIds.equals("")) {
            return ResultMessage.PARAM_MISS;
        }
        String[] strings=courseIds.split("-");
        for (String s : strings) {
            Integer i = Integer.parseInt(s);
            if(delete(i)) num++;
        }
        return num > 0 ? ResultMessage.DELETE_SUCCESS : ResultMessage.DELETE_ERROR;
    }
}
