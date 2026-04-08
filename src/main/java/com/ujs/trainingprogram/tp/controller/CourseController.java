package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.authentication.RequireAuthentication;
import com.ujs.trainingprogram.tp.common.constant.AuthConstant;
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

import java.util.List;

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
    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "管理员添加课程")
    @PostMapping("/api/course/mainAdmin/create")
    public Result<Void> createCourse(@RequestBody CourseSaveReqDTO requestParam) {
        courseService.createCourse(requestParam);
        return Results.success();
    }

    /**
     * 修改课程信息
     */
    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "修改课程信息")
    @PutMapping("/api/course/mainAdmin/update")
    public Result<Void> updateCourse(@RequestBody CourseUpdateReqDTO requestParam) {
        courseService.updateCourse(requestParam);
        return Results.success();
    }

    /**
     * 删除课程，可批量删除
     */
    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "根据id删除课程，可批量")
    @DeleteMapping("/api/course/mainAdmin/delete")
    public Result<Void> deleteCourse(@RequestBody List<String> ids) {
        courseService.deleteCourse(ids);
        return Results.success();
    }

    /**
     * 启用课程，可批量启用
     */
    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "启用课程")
    @PutMapping("/api/course/mainAdmin/enable")
    public Result<Void> enableCourse(@RequestBody List<String> ids) {
        courseService.enableCourse(ids);
        return Results.success();
    }

}
