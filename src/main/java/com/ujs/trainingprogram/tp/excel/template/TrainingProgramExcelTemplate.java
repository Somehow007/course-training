package com.ujs.trainingprogram.tp.excel.template;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.ujs.trainingprogram.tp.excel.converter.CourseNatureConverter;
import lombok.Data;

/**
 * 培养计划Excel导入导出DTO
 */
@Data
@ContentRowHeight(15)
@HeadRowHeight(20)
@ColumnWidth(25)
public class TrainingProgramExcelTemplate {

    /**
     * 课程类别
     */
    @ExcelProperty("课程类别")
    private String courseType;

    /**
     * 课程性质（0:必修 1:选修）
     */
    @ExcelProperty(value = "课程性质", converter = CourseNatureConverter.class)
    private Integer courseNature;

    /**
     * 开课学院名称
     */
    @ExcelProperty("开课学院")
    private String collegeName;

    /**
     * 课程名称
     */
    @ExcelProperty("课程名称")
    private String courseName;

    /**
     * 总学分
     */
    @ExcelProperty("总学分")
    private Float totalCredits;

    /**
     * 总学时
     */
    @ExcelProperty("总学时")
    private String totalHours;

    /**
     * 授课学时
     */
    @ExcelProperty(value = {"各环节学时分配", "授课学时"})
    private Integer hourTeach;

    /**
     * 实验学时
     */
    @ExcelProperty(value = {"各环节学时分配", "实验学时"})
    private Integer hourPractice;

    /**
     * 上机学时
     */
    @ExcelProperty(value = {"各环节学时分配", "上机学时"})
    private Integer hourOperation;

    /**
     * 实践学时
     */
    @ExcelProperty(value = {"各环节学时分配", "实践学时"})
    private Integer hourOutside;

    /**
     * 周学时
     */
    @ExcelProperty(value = "周学时")
    private Integer hourWeek;

    /**
     * 建议修读学期
     */
    @ExcelProperty(value = "建议修读学期")
    private Integer term;

    /**
     * 选修要求学分
     */
    @ExcelProperty(value = "选修要求学分")
    private Integer requiredElective;

    /**
     * 备注
     * 根据备注来进行分组，如果涉及到多选几，必须填写一致的备注
     */
    @ExcelProperty("备注")
    private String remark;

    /**
     * 年份
     */
    @ExcelProperty(value = "年份")
    private Integer year;
}