package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.common.constant.AuthConstant;
import com.ujs.trainingprogram.tp.authentication.RequireAuthentication;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dto.req.college.CollegePageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.college.CollegeUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.college.CollegePageRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 学院业务控制层
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "学院请求管理")
public class CollegeController {

    private final CollegeService collegeService;

    @Operation(summary = "分页查询所有学院")
    @GetMapping("/college/page")
    public Result<IPage<CollegePageRespDTO>> pageColleges(CollegePageReqDTO requestParam) {
        return Results.success(collegeService.pageCollege(requestParam));
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "添加学院")
    @PostMapping("/college/mainAdmin/add")
    public Result<Void> createCollege(@RequestBody CollegeSaveReqDTO requestParam) {
        collegeService.createCollege(requestParam);
        return Results.success();
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "删除学院")
    @DeleteMapping("/college/mainAdmin/delete/{id}")
    public Result<Void> deleteCollege(@PathVariable String id) {
        collegeService.deleteCollege(Long.parseLong(id));
        return Results.success();
    }

    @RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
    @Operation(summary = "修改学院")
    @PutMapping("/college/mainAdmin/update")
    public Result<Void> updateCollege(@RequestBody CollegeUpdateReqDTO requestParam) {
        collegeService.updateCollege(requestParam);
        return Results.success();
    }
}
