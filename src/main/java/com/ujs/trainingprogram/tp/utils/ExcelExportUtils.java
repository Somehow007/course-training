package com.ujs.trainingprogram.tp.utils;

import cn.hutool.core.util.StrUtil;
import com.ujs.trainingprogram.tp.excel.template.TrainingProgramExcelTemplate;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
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
     * 仅对指定工作表应用统一样式（不做纵向合并）
     *
     * @param excelBytes 原始 Excel 字节数组
     * @param sheetIndex 工作表索引（从0开始）
     * @return 应用样式后的字节数组
     */
    public static byte[] applyStylesOnly(byte[] excelBytes, int sheetIndex) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            applyStyles(workbook, sheet);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                workbook.write(baos);
                return baos.toByteArray();
            }
        }
    }

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

            if (lastRowNum >= 1) {
                // 对每一列分别合并
                for (int col : columnsToMerge) {
                    mergeColumn(sheet, col, lastRowNum);
                }
            }

            // 合并后统一设置单元格样式
            applyStyles(workbook, sheet);

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
     * 统一设置导出 Excel 的样式：
     * - 所有单元格无背景色、细边框
     * - 表头（前两行）黑体、居中、9号
     * - 数据行：
     *   - 第1、2列：黑体、居中
     *   - 第3、4列：宋体、左对齐、垂直居中
     *   - 第5列及之后：宋体、居中
     * - “合计/小计”行整行黑体、居中
     */
    private static void applyStyles(Workbook workbook, Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum < 0) {
            return;
        }

        // 表头行数：当前模板为多级表头，两行
        final int headRowCount = 2;

        // 创建字体
        Font simHeiFont = workbook.createFont();
        simHeiFont.setFontName("黑体");
        simHeiFont.setFontHeightInPoints((short) 9);

        Font simSunFont = workbook.createFont();
        simSunFont.setFontName("宋体");
        simSunFont.setFontHeightInPoints((short) 9);

        // 公共：细边框 + 无背景
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFont(simHeiFont);
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headStyle.setFillPattern(FillPatternType.NO_FILL);
        headStyle.setBorderTop(BorderStyle.THIN);
        headStyle.setBorderBottom(BorderStyle.THIN);
        headStyle.setBorderLeft(BorderStyle.THIN);
        headStyle.setBorderRight(BorderStyle.THIN);

        CellStyle boldCenterStyle = workbook.createCellStyle();
        boldCenterStyle.setFont(simHeiFont);
        boldCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        boldCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        boldCenterStyle.setFillPattern(FillPatternType.NO_FILL);
        boldCenterStyle.setBorderTop(BorderStyle.THIN);
        boldCenterStyle.setBorderBottom(BorderStyle.THIN);
        boldCenterStyle.setBorderLeft(BorderStyle.THIN);
        boldCenterStyle.setBorderRight(BorderStyle.THIN);

        CellStyle simSunLeftStyle = workbook.createCellStyle();
        simSunLeftStyle.setFont(simSunFont);
        simSunLeftStyle.setAlignment(HorizontalAlignment.LEFT);
        simSunLeftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        simSunLeftStyle.setFillPattern(FillPatternType.NO_FILL);
        simSunLeftStyle.setBorderTop(BorderStyle.THIN);
        simSunLeftStyle.setBorderBottom(BorderStyle.THIN);
        simSunLeftStyle.setBorderLeft(BorderStyle.THIN);
        simSunLeftStyle.setBorderRight(BorderStyle.THIN);

        CellStyle simSunCenterStyle = workbook.createCellStyle();
        simSunCenterStyle.setFont(simSunFont);
        simSunCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        simSunCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        simSunCenterStyle.setFillPattern(FillPatternType.NO_FILL);
        simSunCenterStyle.setBorderTop(BorderStyle.THIN);
        simSunCenterStyle.setBorderBottom(BorderStyle.THIN);
        simSunCenterStyle.setBorderLeft(BorderStyle.THIN);
        simSunCenterStyle.setBorderRight(BorderStyle.THIN);

        for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            short lastCellNum = row.getLastCellNum();
            if (lastCellNum < 0) {
                continue;
            }

            boolean isHeaderRow = rowIndex < headRowCount;

            // 判断是否合计/小计行：第4列（索引3）文本为“合计”或“小计”
            String courseName = getCellValue(sheet, rowIndex, 3);
            boolean isSummaryRow = "合计".equals(courseName) || "小计".equals(courseName);

            // 合计/小计行：合并第3列和第4列（索引2-3），并将文字放到合并区域左上角（索引2）
            if (!isHeaderRow && isSummaryRow) {
                ensureMergedRegion(sheet, rowIndex, rowIndex, 2, 3);

                Cell leftTop = row.getCell(2);
                if (leftTop == null) {
                    leftTop = row.createCell(2);
                }
                leftTop.setCellValue(courseName);

                Cell right = row.getCell(3);
                if (right != null) {
                    right.setBlank();
                }
            }

            for (int colIndex = 0; colIndex < lastCellNum; colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) {
                    continue;
                }

                if (isHeaderRow) {
                    cell.setCellStyle(headStyle);
                } else if (isSummaryRow) {
                    cell.setCellStyle(boldCenterStyle);
                } else {
                    if (colIndex == 0 || colIndex == 1) {
                        cell.setCellStyle(boldCenterStyle);
                    } else if (colIndex == 2 || colIndex == 3) {
                        cell.setCellStyle(simSunLeftStyle);
                    } else {
                        cell.setCellStyle(simSunCenterStyle);
                    }
                }
            }
        }
    }

    private static void ensureMergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.getFirstRow() == firstRow
                    && region.getLastRow() == lastRow
                    && region.getFirstColumn() == firstCol
                    && region.getLastColumn() == lastCol) {
                return;
            }
        }
        try {
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
        } catch (Exception ignored) {
            // 忽略合并冲突
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
