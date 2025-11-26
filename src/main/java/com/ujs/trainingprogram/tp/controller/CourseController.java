package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dto.req.course.CoursePageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.req.course.CourseSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.course.CourseUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.course.CoursePageQueryRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 课程业务控制层
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "课程管理")
public class CourseController {

    private final CourseService courseService;

    /**
     * 分页查询所有课程
     */
    @Operation(summary = "分页查询所有课程")
    @GetMapping("/api/course/mainAdmin/page")
    public Result<IPage<CoursePageQueryRespDTO>> pageCourse(CoursePageQueryReqDTO requestParam) {
        return Results.success(courseService.pageQueryCourse(requestParam));
    }

    /**
     * 添加任意学院的课程
     */
    @Operation(summary = "管理员添加课程")
    @PostMapping("/api/course/mainAdmin/create")
    public Result<Void> createCourse(@RequestBody CourseSaveReqDTO requestParam) {
        courseService.createCourse(requestParam);
        return Results.success();
    }

    /**
     * 修改课程信息
     */
    @Operation(summary = "修改课程信息")
    @PutMapping("/api/course/mainAdmin/update")
    public Result<Void> updateCourse(@RequestBody CourseUpdateReqDTO requestParam) {
        courseService.updateCourse(requestParam);
        return Results.success();
    }

    /**
     * 删除课程
     */
    @Operation(summary = "根据id删除课程")
    @DeleteMapping("/api/course/mainAdmin/delete/{id}")
    public Result<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(Long.parseLong(id));
        return Results.success();
    }

    /**
     * 批量删除课程
     */
//    @DeleteMapping("/course/mainAdmin/deleteAll")
//    public ResultMessage deleteAllCourse(@RequestParam(value = "course_ids",defaultValue = "") String courseIds) {
//        int num=0;
//        //获取参数
//        if (courseIds.equals("")) {
//            return ResultMessage.PARAM_MISS;
//        }
//        String[] strings=courseIds.split("-");
//        for (String s : strings) {
//            Integer i = Integer.parseInt(s);
//            if(delete(i)) num++;
//        }
//        return num > 0 ? ResultMessage.DELETE_SUCCESS : ResultMessage.DELETE_ERROR;
//    }
}
