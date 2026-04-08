package com.ujs.trainingprogram.tp.controller;

import com.ujs.trainingprogram.tp.authentication.RequireAuthentication;
import com.ujs.trainingprogram.tp.common.constant.AuthConstant;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivityAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivitySaveReqDTO;
import com.ujs.trainingprogram.tp.service.CourseExclusivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程分组管理层
 * 非对外接口
 */
@RestController
@Tag(name = "课程分组管理")
@RequiredArgsConstructor
public class CourseExclusivityController {
    
    private final CourseExclusivityService exclusivityService;

    /**
     * 创建课程分组
     */
    @RequireAuthentication(AuthConstant.DEPARTMENT_CHAIR)
    @Operation(summary = "创建课程分组")
    @PostMapping("/api/course-exclusivity/mainAdmin/create")
    public Result<Void> createTrainingProgram(@RequestBody CourseExclusivitySaveReqDTO requestParam) {
        exclusivityService.createCourseExclusivity(requestParam);
        return Results.success();
    }

    /**
     * 为课程分组添加课程信息
     */
    @RequireAuthentication(AuthConstant.DEPARTMENT_CHAIR)
    @Operation(summary = "为课程分组添加课程信息")
    @PostMapping("/api/course-exclusivity/mainAdmin/add")
    public Result<Void> addCourseToTrainingProgram(@RequestBody CourseExclusivityAddCourseReqDTO requestParam) {
        exclusivityService.addCourseToCourseExclusivity(requestParam);
        return Results.success();
    }
//
//    /**
//     * 查询某培养计划的课程分组
//     */
//    @Operation(summary = "查询课程分组")
//    @GetMapping("/api/course-exclusivity/detail/select/{id}")
//    public Result<List<TrainingProgramDetailSelectRespDTO>> selectTrainingProgramDetail(@PathVariable("id") String id) {
//        return Results.success(exclusivityService.selectTrainingProgramDetail(id));
//    }


//    /**
//     * 修改课程分组
//     */
//    @Operation(summary = "修改课程分组")
//    @PostMapping("/api/training-program/mainAdmin/update")
//    public Result<Void> updateTrainingProgram(@RequestBody TrainingProgramUpdateReqDTO requestParam) {
//        exclusivityService.updateTrainingProgram(requestParam);
//        return Results.success();
//    }
}
