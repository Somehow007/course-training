package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dto.req.major.MajorPageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorSaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.major.MajorUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.major.MajorPageRespDTO;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 专业业务控制层
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "专业请求管理")
public class MajorController {

    private final MajorService majorService;

    private final CollegeService collegeService;

    /**
     * 添加专业
     */
    @Operation(summary = "添加专业")
    @PostMapping("/api/major/mainAdmin/add")
    public Result<Void> createMajor(@RequestBody MajorSaveReqDTO requestParam) {
        majorService.createMajor(requestParam);
        return Results.success();
    }

    /**
     * 删除专业
     */
    @Operation(summary = "删除专业")
    @DeleteMapping("/api/major/mainAdmin/delete/{id}")
    public Result<Void> deleteMajor(@PathVariable String id) {
        majorService.deleteMajor(Long.parseLong(id));
        return Results.success();
    }

    /**
     * 修改专业
     */
    @Operation(summary = "修改专业")
    @PutMapping("/api/major/mainAdmin/update")
    public Result<Void> updateMajor(@RequestBody MajorUpdateReqDTO requestParam) {
        majorService.updateMajor(requestParam);
        return Results.success();
    }

    /**
     * 分页查询专业信息
     */
    @Operation(summary = "分页查询专业信息")
    @GetMapping("/api/major/page")
    public Result<IPage<MajorPageRespDTO>> pageQueryMajor(MajorPageReqDTO requestParam) {
        return Results.success(majorService.pageMajors(requestParam));
    }
}
