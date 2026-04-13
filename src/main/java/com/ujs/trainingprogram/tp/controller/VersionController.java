package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.authentication.RequireAuthentication;
import com.ujs.trainingprogram.tp.common.constant.AuthConstant;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dao.entity.VersionChangeLogDO;
import com.ujs.trainingprogram.tp.dto.req.version.VersionCompareReqDTO;
import com.ujs.trainingprogram.tp.dto.req.version.VersionCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.version.VersionPageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.version.VersionSaveChangesReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramDetailSelectRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.version.VersionCompareRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.version.VersionListRespDTO;
import com.ujs.trainingprogram.tp.service.VersionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "版本管理")
@RequiredArgsConstructor
@RequestMapping("/api/version")
public class VersionController {

    private final VersionHistoryService versionHistoryService;

    @Operation(summary = "分页查询版本历史")
    @GetMapping("/page")
    public Result<IPage<VersionListRespDTO>> pageVersionHistory(VersionPageReqDTO requestParam) {
        return Results.success(versionHistoryService.pageVersionHistory(requestParam));
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "创建新版本")
    @PostMapping("/create")
    public Result<Void> createVersion(@RequestBody VersionCreateReqDTO requestParam) {
        versionHistoryService.createVersion(requestParam);
        return Results.success();
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "发布版本")
    @PostMapping("/publish/{versionId}")
    public Result<Void> publishVersion(@PathVariable String versionId) {
        versionHistoryService.publishVersion(versionId);
        return Results.success();
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "回滚版本")
    @PostMapping("/rollback/{versionId}")
    public Result<Void> rollbackVersion(@PathVariable String versionId) {
        versionHistoryService.rollbackVersion(versionId);
        return Results.success();
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "归档版本")
    @PostMapping("/archive/{versionId}")
    public Result<Void> archiveVersion(@PathVariable String versionId) {
        versionHistoryService.archiveVersion(versionId);
        return Results.success();
    }

    @Operation(summary = "版本对比")
    @PostMapping("/compare")
    public Result<VersionCompareRespDTO> compareVersions(@RequestBody VersionCompareReqDTO requestParam) {
        return Results.success(versionHistoryService.compareVersions(requestParam));
    }

    @Operation(summary = "查询版本详情")
    @GetMapping("/detail/{versionId}")
    public Result<VersionListRespDTO> getVersionDetail(@PathVariable String versionId) {
        return Results.success(versionHistoryService.getVersionDetail(versionId));
    }

    @Operation(summary = "查询版本快照详情数据")
    @GetMapping("/snapshot-detail/{versionId}")
    public Result<List<TrainingProgramDetailSelectRespDTO>> getVersionSnapshotDetail(@PathVariable String versionId) {
        return Results.success(versionHistoryService.getVersionSnapshotDetail(versionId));
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "删除版本")
    @DeleteMapping("/delete/{versionId}")
    public Result<Void> deleteVersion(@PathVariable String versionId) {
        versionHistoryService.deleteVersion(versionId);
        return Results.success();
    }

    @Operation(summary = "查询培养方案的版本列表")
    @GetMapping("/list-by-program/{trainingProgramId}")
    public Result<List<VersionListRespDTO>> listVersionsByTrainingProgramId(@PathVariable String trainingProgramId) {
        return Results.success(versionHistoryService.listVersionsByTrainingProgramId(trainingProgramId));
    }

    @Operation(summary = "查询版本变更日志")
    @GetMapping("/change-logs/{versionId}")
    public Result<List<VersionChangeLogDO>> getVersionChangeLogs(@PathVariable String versionId) {
        return Results.success(versionHistoryService.getVersionChangeLogs(versionId));
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "保存修改并创建新版本")
    @PostMapping("/save-changes")
    public Result<Void> saveChangesAndCreateVersion(@RequestBody VersionSaveChangesReqDTO requestParam) {
        versionHistoryService.saveChangesAndCreateVersion(requestParam);
        return Results.success();
    }

    @Operation(summary = "导出版本到Excel")
    @GetMapping("/export/{versionId}")
    public void exportVersionToExcel(@PathVariable String versionId, HttpServletResponse response) {
        versionHistoryService.exportVersionToExcel(versionId, response);
    }
}
