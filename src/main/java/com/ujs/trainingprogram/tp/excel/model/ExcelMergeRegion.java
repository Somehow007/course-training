package com.ujs.trainingprogram.tp.excel.model;

import lombok.Data;

/**
 * Excel合并区域信息
 */
@Data
public class ExcelMergeRegion {

    /**
     * 起始行（0-based）
     */
    private int firstRow;

    /**
     * 结束行（0-based）
     */
    private int lastRow;

    /**
     * 合并区域内的值
     */
    private String value;
}
