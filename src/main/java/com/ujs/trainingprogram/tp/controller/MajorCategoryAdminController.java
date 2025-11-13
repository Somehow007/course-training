//package com.ujs.trainingprogram.tp.controller;
//
//import com.ujs.trainingprogram.tp.common.result.Result;
//import com.ujs.trainingprogram.tp.common.web.Results;
//import com.ujs.trainingprogram.tp.dto.resp.MajorCategoryQueryRespDTO;
//import com.ujs.trainingprogram.tp.service.MajorCategoryService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
///**
// * 专业分类管理员控制层
// */
//@RestController
//@RequiredArgsConstructor
//@Tag(name = "专业配置管理")
//public class MajorCategoryAdminController {
//
//    private final MajorCategoryService majorCategoryService;
//
//    /**
//     * 列表查询专业分配配置信息
//     */
//    @Operation(summary = "列表查询专业分配配置信息")
//    @GetMapping("/major-category/list")
//    public Result<List<MajorCategoryQueryRespDTO>> listMajorCategory() {
//        return Results.success(majorCategoryService.listMajorCategories());
//    }
//}
