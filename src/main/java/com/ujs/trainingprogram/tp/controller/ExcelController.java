package com.ujs.trainingprogram.tp.controller;

import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.service.TrainingProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Excel业务处理控制层
 */
@RestController
@Tag(name = "Excel管理")
@RequiredArgsConstructor
public class ExcelController {

    private final TrainingProgramService trainingProgramService;

    /**
     * 导出培养计划到Excel
     */
    @Operation(summary = "导出培养计划到Excel")
    @GetMapping("/api/training-program/export/{id}")
    public void exportTrainingProgramToExcel(@PathVariable("id") String id, HttpServletResponse response) {
        trainingProgramService.exportTrainingProgramToExcel(id, response);
    }

    /**
     * 从Excel导入培养计划
     */
    @Operation(summary = "从Excel导入培养计划")
    @PostMapping("/api/training-program/import")
    public Result<Void> importTrainingProgramFromExcel(
            @RequestParam(value = "collegeId") String collegeId,
            @RequestParam(value = "majorId") String majorId,
            @RequestParam("file") MultipartFile file) {

        trainingProgramService.importTrainingProgramFromExcel(file, collegeId, majorId);
        return Results.success();
    }
    
    /**
     * 下载培养计划Excel模板
     */
    @Operation(summary = "下载培养计划Excel模板")
    @GetMapping("/api/training-program/template")
    public void downloadTrainingProgramTemplate(HttpServletResponse response) {
        trainingProgramService.downloadTrainingProgramTemplate(response);
    }
}