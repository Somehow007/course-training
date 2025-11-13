package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.common.result.ResultMessage;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dao.entity.CollegeDO;
import com.ujs.trainingprogram.tp.dao.entity.CourseDO;
import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseCategoryEnum;
import com.ujs.trainingprogram.tp.dao.entity.enums.CourseTypeEnum;
import com.ujs.trainingprogram.tp.dto.req.course.CoursePageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.course.CourseSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.course.CoursePageRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "课程管理")
public class CourseController {

    private final CourseService courseService;

    private final MajorService majorService;

    private final CollegeService collegeService;

    @Operation(summary = "分页查询所有课程")
    @GetMapping("/course/page")
    public Result<IPage<CoursePageRespDTO>> pageCourse(CoursePageReqDTO requestParam) {

        return Results.success(courseService.pageCourse(requestParam));
    }

    /**
     * 添加任意学院的课程
     */
    @Operation(summary = "管理员为任意学院添加课程")
    @PostMapping("/course/mainAdmin/add")
    public Result<Void> addCourse(CourseSaveReqDTO requestParam) {

        return Results.success();
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

//        courseDO.setCourseCode(courseCode);
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
        String collegeId = collegeService.getCollegeLikeByName(collegeName).get(0).getCollegeCode();
        String majorId = majorService.getMajorLikeByName(majorName).get(0).getMajorCode();
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
