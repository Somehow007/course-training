package com.ujs.trainingprogram.tp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ujs.trainingprogram.tp.common.result.Result;
import com.ujs.trainingprogram.tp.common.web.Results;
import com.ujs.trainingprogram.tp.dto.req.sysdict.SysDictCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.sysdict.SysDictPageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.sysdict.SysDictPageQueryRespDTO;
import com.ujs.trainingprogram.tp.service.SysDictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 系统字典业务控制层
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "系统字典业务控制层")
public class SysDictController {

    private final SysDictService sysDictService;

    @Operation(summary = "创建系统字典")
    @PostMapping("/api/sys-dict/mainAdmin/create")
    public Result<Void> createSysDict(@RequestBody SysDictCreateReqDTO requestParam) {
        sysDictService.createSysDict(requestParam);
        return Results.success();
    }

    @Operation(summary = "删除系统字典")
    @DeleteMapping("/api/sys-dict/mainAdmin/delete/{id}")
    public Result<Void> deleteSysDict(@PathVariable String id) {
        sysDictService.deleteSysDict(Long.parseLong(id));
        return Results.success();
    }

    @Operation(summary = "分页查询系统字典")
    @GetMapping("/api/sys-dict/page")
    public Result<IPage<SysDictPageQueryRespDTO>> pageQuerySysDict(SysDictPageQueryReqDTO requestParam) {
        return Results.success(sysDictService.pageQuerySysDict(requestParam));
    }
}





