package com.ujs.trainingprogram.tp.utils;

import cn.hutool.core.util.StrUtil;
import com.ujs.trainingprogram.tp.excel.template.TrainingProgramExcelTemplate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Excel 导出工具（支持多列、连续相同值）
 */
public class ExcelExportUtils {

    /**
     * 合并指定列的连续相同单元格
     *
     * @param excelBytes   原始 Excel 字节数组
     * @param sheetIndex   工作表索引（从0开始）
     * @param columnsToMerge 要合并的列索引（0-based），如：0, 1, 12
     * @return 合并后的字节数组
     */
    public static byte[] mergeConsecutiveSameCells(byte[] excelBytes, int sheetIndex, int... columnsToMerge) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int lastRowNum = sheet.getLastRowNum();

            if (lastRowNum < 1) {
                return excelBytes; // 无数据或只有表头
            }

            // 对每一列分别合并
            for (int col : columnsToMerge) {
                mergeColumn(sheet, col, lastRowNum);
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                workbook.write(baos);
                return baos.toByteArray();
            }
        }
    }

    /**
     * 合并单列中的连续相同值（跳过表头）
     */
    private static void mergeColumn(Sheet sheet, int columnIndex, int lastRowNum) {
        int startRow = 1; // 数据从第2行开始（0-based=1）

        for (int currentRow = 1; currentRow <= lastRowNum; currentRow++) {
            String currentValue = getCellValue(sheet, currentRow, columnIndex);
            String nextValue = (currentRow < lastRowNum)
                    ? getCellValue(sheet, currentRow + 1, columnIndex)
                    : null;

            // 当前行与下一行不同，或到达最后一行 → 合并 [startRow, currentRow]
            if (nextValue == null || !Objects.equals(currentValue, nextValue)) {
                if (currentRow > startRow) {
                    // 添加合并区域（注意：POI 不允许重叠合并，但连续合并不会重叠）
                    String firstValue = getCellValue(sheet, startRow, columnIndex);
                    if (StrUtil.isNotBlank(firstValue)) {
                        try {
                            sheet.addMergedRegion(new CellRangeAddress(startRow, currentRow, columnIndex, columnIndex));
                        } catch (Exception e) {
                            // 忽略合并冲突（极少见）
                        }
                    }
                }
                startRow = currentRow + 1;
            }
        }
    }

    /**
     * 安全获取单元格字符串值
     */
    private static String getCellValue(Sheet sheet, int rowIndex, int columnIndex) {
        var row = sheet.getRow(rowIndex);
        if (row == null) return "";
        var cell = row.getCell(columnIndex);
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    return String.valueOf((int) val);
                }
                return String.valueOf(val);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 插入合计与小计
     *
     * @param originalList  排序后的数据
     * @return              最终版本
     */
    public static List<TrainingProgramExcelTemplate> insertSummaryRows(List<TrainingProgramExcelTemplate> originalList) {
        if (originalList == null || originalList.isEmpty()) {
            return originalList;
        }

        List<TrainingProgramExcelTemplate> result = new ArrayList<>();
        String currentCourseType = originalList.get(0).getCourseType();
        Integer currentCourseNature = originalList.get(0).getCourseNature();

        // 第一级累计
        Float totalCreditsSum = 0f;

        Integer hourTeachSum = 0;
        Integer hourPracticeSum = 0;
        Integer hourOperationSum = 0;
        Integer hourOutsideSum = 0;
        Integer hourWeekSum = 0;
        double totalHoursSum = 0.0;   // 小时
        double totalWeeksSum = 0.0;   // 周

        // 第二级累计
        Float subTotalCreditsSum = 0f;
        Integer subHourTeachSum = 0;
        Integer subHourPracticeSum = 0;
        Integer subHourOperationSum = 0;
        Integer subHourOutsideSum = 0;
        Integer subHourWeekSum = 0;
        double subTotalHoursSum = 0.0;
        double subTotalWeeksSum = 0.0;

        for (int i = 0; i < originalList.size(); i++) {
            TrainingProgramExcelTemplate item = originalList.get(i);

            // === 累加当前行 ===
            if (item.getTotalCredits() != null) {
                totalCreditsSum += item.getTotalCredits();
                subTotalCreditsSum += item.getTotalCredits();
            }
            if (item.getHourTeach() != null) {
                hourTeachSum += item.getHourTeach();
                subHourTeachSum += item.getHourTeach();
            }
            if (item.getHourPractice() != null) {
                hourPracticeSum += item.getHourPractice();
                subHourPracticeSum += item.getHourPractice();
            }
            if (item.getHourOperation() != null) {
                hourOperationSum += item.getHourOperation();
                subHourOperationSum += item.getHourOperation();
            }
            if (item.getHourOutside() != null) {
                hourOutsideSum += item.getHourOutside();
                subHourOutsideSum += item.getHourOutside();
            }
            if (item.getHourWeek() != null) {
                hourWeekSum += item.getHourWeek();
                subHourWeekSum += item.getHourWeek();
            }

            String currentTotalHours = item.getTotalHours();
            if (currentTotalHours != null && !"-".equals(currentTotalHours)) {
                double[] parsed = parseTotalHours(currentTotalHours);
                totalHoursSum += parsed[0];
                totalWeeksSum += parsed[1];
                subTotalHoursSum += parsed[0];
                subTotalWeeksSum += parsed[1];
            }

            // === 添加当前数据行 ===
            result.add(item);

            // === 检查下一行 ===
            boolean isLast = (i == originalList.size() - 1);
            String nextType = isLast ? null : originalList.get(i + 1).getCourseType();
            Integer nextNature = isLast ? null : originalList.get(i + 1).getCourseNature();

            boolean typeChanges = !Objects.equals(currentCourseType, nextType);
            boolean natureChanges = Objects.equals(currentCourseType, nextType) &&
                    !Objects.equals(currentCourseNature, nextNature);

            // 小计：同一类型内性质切换
            if (natureChanges) {
                result.add(buildSubtotalRow(
                        currentCourseType, currentCourseNature,
                        subTotalCreditsSum, subHourTeachSum, subHourPracticeSum,
                        subHourOperationSum, subHourOutsideSum, subHourWeekSum,
                        subTotalHoursSum, subTotalWeeksSum
                ));
                // 重置 subSum
                subTotalCreditsSum = 0f;
                subHourTeachSum = 0;
                subHourPracticeSum = 0;
                subHourOperationSum = 0;
                subHourOutsideSum = 0;
                subHourWeekSum = 0;
                currentCourseNature = nextNature;
            }

            // 合计：类型切换 或 最后一行
            if (typeChanges || isLast) {
                // 如果 subSum 还有值（如最后一组只有必修），先加小计
                if (subTotalCreditsSum > 0 || subHourTeachSum > 0) {
                    result.add(buildSubtotalRow(
                            currentCourseType, currentCourseNature,
                            subTotalCreditsSum, subHourTeachSum, subHourPracticeSum,
                            subHourOperationSum, subHourOutsideSum, subHourWeekSum,
                            subTotalHoursSum, subTotalWeeksSum
                    ));
                    subTotalCreditsSum = 0f;
                    subHourTeachSum = 0;
                    subHourPracticeSum = 0;
                    subHourOperationSum = 0;
                    subHourOutsideSum = 0;
                    subHourWeekSum = 0;
                    subTotalHoursSum = 0.0;
                    subTotalWeeksSum = 0.0;
                }
                // 加合计
                result.add(buildTotalRow(
                        currentCourseType,
                        totalCreditsSum, hourTeachSum, hourPracticeSum,
                        hourOperationSum, hourOutsideSum, hourWeekSum,
                        totalHoursSum, totalWeeksSum
                ));
                // 重置总计
                totalCreditsSum = 0f;
                hourTeachSum = 0;
                hourPracticeSum = 0;
                hourOperationSum = 0;
                hourOutsideSum = 0;
                hourWeekSum = 0;
                totalHoursSum = 0.0;
                totalWeeksSum = 0.0;

                if (!isLast) {
                    currentCourseType = nextType;
                    currentCourseNature = nextNature;
                }
            }
        }

        return result;
    }

    private static TrainingProgramExcelTemplate buildTotalRow(
            String courseType,
            Float totalCredits, Integer hourTeach, Integer hourPractice,
            Integer hourOperation, Integer hourOutside, Integer hourWeek,
            double totalHours, double totalWeeks) {

        TrainingProgramExcelTemplate total = new TrainingProgramExcelTemplate();
        total.setCourseType(courseType);
        total.setCourseNature(null); // 合计行无课程性质
        total.setCollegeName("");    // 留空（用于合并）
        total.setCourseName("合计");
        total.setTotalCredits(totalCredits);
        total.setTotalHours(formatTotalHours(totalHours, totalWeeks));
        total.setHourTeach(hourTeach);
        total.setHourPractice(hourPractice);
        total.setHourOperation(hourOperation);
        total.setHourOutside(hourOutside);
        total.setHourWeek(hourWeek);
        // 其他字段留空
        return total;
    }

    private static TrainingProgramExcelTemplate buildSubtotalRow(
            String courseType,
            Integer courseNature,
            Float totalCredits, Integer hourTeach, Integer hourPractice,
            Integer hourOperation, Integer hourOutside, Integer hourWeek,
            double subtotalHours, double subtotalWeeks) {

        TrainingProgramExcelTemplate subtotal = new TrainingProgramExcelTemplate();
        subtotal.setCourseType(courseType);
        subtotal.setCourseNature(courseNature);
        subtotal.setCollegeName("");
        subtotal.setCourseName("小计");
        subtotal.setTotalCredits(totalCredits);
        subtotal.setTotalHours(formatTotalHours(subtotalHours, subtotalWeeks));
        subtotal.setHourTeach(hourTeach);
        subtotal.setHourPractice(hourPractice);
        subtotal.setHourOperation(hourOperation);
        subtotal.setHourOutside(hourOutside);
        subtotal.setHourWeek(hourWeek);
        return subtotal;
    }

    /**
     * 解析 totalHours 字符串，返回 [hours, weeks]
     */
    private static double[] parseTotalHours(String totalHours) {
        if (totalHours == null || totalHours.trim().isEmpty() || "-".equals(totalHours)) {
            return new double[]{0.0, 0.0};
        }

        String text = totalHours.trim();
        double hours = 0.0;
        double weeks = 0.0;

        // 情况1: 纯数字（如 "48"）→ 小时
        if (text.matches("^\\d+(\\.\\d+)?$")) {
            hours = Double.parseDouble(text);
            return new double[]{hours, weeks};
        }

        // 情况2: 带“周”（如 "2.5周"）
        if (text.endsWith("周")) {
            String numPart = text.substring(0, text.length() - 1).trim();
            if (numPart.matches("^\\d+(\\.\\d+)?$")) {
                weeks = Double.parseDouble(numPart);
                return new double[]{hours, weeks};
            }
        }

        // 情况3: 混合格式（如 "48/2.5周"）
        if (text.contains("/")) {
            String[] parts = text.split("/", 2);
            String hourPart = parts[0].trim();
            String weekPart = parts[1].trim();

            if (hourPart.matches("^\\d+(\\.\\d+)?$")) {
                hours = Double.parseDouble(hourPart);
            }
            if (weekPart.endsWith("周")) {
                String weekNum = weekPart.substring(0, weekPart.length() - 1).trim();
                if (weekNum.matches("^\\d+(\\.\\d+)?$")) {
                    weeks = Double.parseDouble(weekNum);
                }
            }
            return new double[]{hours, weeks};
        }

        // 其他无法识别的格式 → 忽略
        return new double[]{0.0, 0.0};
    }

    private static String formatTotalHours(double hours, double weeks) {
        // 清理极小值（浮点误差）
        if (Math.abs(hours) < 0.001) hours = 0.0;
        if (Math.abs(weeks) < 0.001) weeks = 0.0;

        if (hours > 0 && weeks > 0) {
            // 保留一位小数（如 80/2.5周）
            String h = (hours == (long) hours) ? String.valueOf((long) hours) : String.format("%.1f", hours);
            String w = (weeks == (long) weeks) ? String.valueOf((long) weeks) : String.format("%.1f", weeks);
            return h + "/" + w + "周";
        } else if (weeks > 0) {
            String w = (weeks == (long) weeks) ? String.valueOf((long) weeks) : String.format("%.1f", weeks);
            return w + "周";
        } else if (hours > 0) {
            return String.valueOf((long) hours); // 整数小时
        } else {
            return "-";
        }
    }
}
