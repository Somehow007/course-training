//package com.ujs.trainingprogram.tp.dao.entity;
//
//import com.baomidou.mybatisplus.annotation.*;
//import com.ujs.trainingprogram.tp.common.database.BaseDO;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
///**
// * 专业分类配置实体
// */
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@TableName("major_category")
//public class MajorCategoryDO extends BaseDO {
//
//    /**
//     * 自增主键，供 major 表外键引用
//     */
//    @TableId(type = IdType.AUTO)
//    private Long id;
//
//    /**
//     * 学科门类，如“工学”
//     */
//    private String disciplineCategory;
//
//    /**
//     * 专业类别，如“计算机类”
//     */
//    private String professionalCategory;
//
//    /**
//     * 描述（可选）
//     */
//    private String description;
//
//    /**
//     * 排序值
//     */
//    private Integer sortOrder;
//
//}