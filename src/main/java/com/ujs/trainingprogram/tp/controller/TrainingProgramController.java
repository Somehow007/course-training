package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.authentication.RequireAuthentication;
import com.ujs.trainingprogram.tp.common.constant.AuthConstant;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramPageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramDetailSelectRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramPageRespDTO;
import com.ujs.trainingprogram.tp.service.TrainingProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 培养计划管理层
 */
@RestController
@Tag(name = "培养计划管理")
@RequiredArgsConstructor
public class TrainingProgramController {

    private final TrainingProgramService trainingProgramService;

    /**
     * 分页查询培养计划
     */
    @Operation(summary = "分页查询培养计划")
    @GetMapping("/api/training-program/page")
    public Result<IPage<TrainingProgramPageRespDTO>> pageQueryTrainingProgram(TrainingProgramPageReqDTO requestParam) {
        return Results.success(trainingProgramService.pageTrainingPrograms(requestParam));
    }

    /**
     * 创建某专业的培养计划
     */
    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "创建某专业的培养计划")
    @PostMapping("/api/training-program/mainAdmin/create")
    public Result<Void> createTrainingProgram(@RequestBody TrainingProgramCreateReqDTO requestParam) {
        trainingProgramService.createTrainingProgram(requestParam);
        return Results.success();
    }

    /**
     * 为培养计划添加课程
     */
    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "为培养计划添加课程")
    @PostMapping("/api/training-program/mainAdmin/add")
    public Result<Void> addCourseToTrainingProgram(@RequestBody TrainingProgramAddCourseReqDTO requestParam) {
        trainingProgramService.addCourseToTrainingProgram(requestParam);
        return Results.success();
    }

    /**
     * 查询某专业的培养计划
     */
    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "查询某专业的培养计划")
    @GetMapping("/api/training-program/detail/select/{id}")
    public Result<List<TrainingProgramDetailSelectRespDTO>> selectTrainingProgramDetail(@PathVariable("id") String id) {
        return Results.success(trainingProgramService.selectTrainingProgramDetail(id));
    }

    /**
     * 删除培养计划
     */
    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "删除培养计划")
    @DeleteMapping("/api/training-program/mainAdmin/delete/{id}")
    public Result<Void> deleteTrainingProgram(@PathVariable("id") String id) {
        trainingProgramService.deleteTrainingProgram(id);
        return Results.success();
    }

    /**
     * 修改培养计划
     */
    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "修改培养计划")
    @PostMapping("/api/training-program/mainAdmin/update")
    public Result<Void> updateTrainingProgram(@RequestBody TrainingProgramUpdateReqDTO requestParam) {
        trainingProgramService.updateTrainingProgram(requestParam);
        return Results.success();
    }

}
