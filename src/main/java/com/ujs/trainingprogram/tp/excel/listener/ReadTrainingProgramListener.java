package com.ujs.trainingprogram.tp.excel.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ujs.trainingprogram.tp.common.errorcode.IErrorCode;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.excel.model.ExcelMergeRegion;
import com.ujs.trainingprogram.tp.excel.template.TrainingProgramExcelTemplate;
import com.ujs.trainingprogram.tp.service.TrainingProgramService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 培养计划读取 Excel 监听器
 */
@Slf4j
@RequiredArgsConstructor
public class ReadTrainingProgramListener extends AnalysisEventListener<TrainingProgramExcelTemplate> {
    
    private final TrainingProgramService trainingProgramService;

    private final Long majorId;
    private final Long tpId;
    private final int version;
    
    /**
     * 批量处理数据，每次处理100条
     */
    private static final int BATCH_SIZE = 100;
    
    /**
     * 临时存储解析的数据
     */
    private final List<TrainingProgramExcelTemplate> dataList = new ArrayList<>();
    
    /**
     * 统计信息
     */
    @Getter
    private int successCount = 0;
    @Getter
    private int failureCount = 0;
    @Getter
    private final List<String> errorMessages = new ArrayList<>();

    /**
     * 行号到合并区域的映射（0-based行号 -> 合并区域）
     */
    private final Map<Integer, ExcelMergeRegion> mergeRegionMap;

    /**
     * 每解析一行数据调用此方法
     * 
     * @param data Excel数据对象
     * @param context  解析上下文
     */
    @Override
    public void invoke(TrainingProgramExcelTemplate data, AnalysisContext context) {
        try {
            // 设置 Excel 行号
            data.setExcelRowIndex(context.readRowHolder().getRowIndex());

            // 数据验证
            validateData(data, context);

            // 添加分组信息
            enrichDataWithGroupInfo(data);

            // 添加到批量处理列表
            dataList.add(data);
            
            // 达到批次大小时进行批量处理
            if (dataList.size() >= BATCH_SIZE) {
                saveData();
                dataList.clear();
            }
        } catch (Exception e) {
            failureCount++;
            String errorMsg = "第" + context.readRowHolder().getRowIndex() + "行数据处理失败: " + e.getMessage();
            errorMessages.add(errorMsg);
            log.error(errorMsg, e);
        }
    }

    /**
     * 所有数据解析完成后调用
     * 
     * @param context 解析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 处理剩余的数据
        if (!dataList.isEmpty()) {
            saveData();
        }

        // 记录导入结果
        log.info("Excel导入完成，成功处理{}条数据，失败{}条", successCount, failureCount);
        if (!errorMessages.isEmpty()) {
            log.warn("导入过程中的错误信息：");
            errorMessages.forEach(log::warn);
            throw new ClientException("导入失败，共发现 " + errorMessages.size() + " 个错误:\n" +
                    String.join("\n", errorMessages));        }
    }
    
    /**
     * 数据验证
     * 
     * @param data Excel数据对象
     * @param context  解析上下文
     */
    private void validateData(TrainingProgramExcelTemplate data, AnalysisContext context) {
        List<String> validationErrors = new ArrayList<>();
        
        // 验证必填字段
        if (StrUtil.isBlank(data.getCourseName())) {
            validationErrors.add("课程名称不能为空");
        }
        
        if (StrUtil.isBlank(data.getTotalHours())) {
            validationErrors.add("总学时不能为空");
        }
        
        // 验证总学时格式
        if (StrUtil.isNotBlank(data.getTotalHours())) {
            String totalTime = data.getTotalHours();
            if (!totalTime.matches("\\d+(\\.\\d+)?(周)?$")) {
                validationErrors.add("总学时格式不正确，应为数字或数字+周的形式");
            }
        }
        
        if (!CollUtil.isEmpty(validationErrors)) {
            throw new ClientException("第" + context.readRowHolder().getRowIndex() + "行数据验证失败: " + 
                                    String.join(", ", validationErrors));
        }
    }
    
    /**
     * 保存数据到数据库
     */
    private void saveData() {
        try {

            // 调用服务层的批量保存方法
            trainingProgramService.batchSaveTrainingProgramDetailsFromExcel(dataList, tpId, majorId, version);
            
            successCount += dataList.size();
            log.info("成功保存{}条培养计划详情数据", dataList.size());
        } catch (Exception e) {
            failureCount += dataList.size();
            String errorMsg = "批量保存数据失败: " + e.getMessage();
            errorMessages.add(errorMsg);
            log.error(errorMsg, e);

        }
    }

    /**
     * 为数据添加分组信息
     */
    private void enrichDataWithGroupInfo(TrainingProgramExcelTemplate data) {

        // Excel行号是1-based，实际从行号为 2 开始解析数据
        int zeroBasedRowIndex = data.getExcelRowIndex();

        // 1. 在合并区域内（多课程分组）
        ExcelMergeRegion mergeRegion = mergeRegionMap.get(zeroBasedRowIndex);

        if (mergeRegion != null && StrUtil.isNotBlank(mergeRegion.getValue())) {
            // 该行属于某个合并区域，生成分组编码
            String groupCode = "EXC_GROUP_" + tpId + "_" + version + "_" +
                    (mergeRegion.getFirstRow() + 1) + "_" + (mergeRegion.getLastRow() + 1);
            data.setElectiveGroupCode(groupCode);
            data.setElectiveRequiredCredits(parseCredit(mergeRegion.getValue()));
            data.setElectiveCreditRequirement(StrUtil.isNotBlank(data.getElectiveCreditRequirement()) ? data.getElectiveCreditRequirement() : mergeRegion.getValue());
        }
        // 2. 没有合并但是有学分要求（单课程分组）
        else if (StrUtil.isNotBlank(data.getElectiveCreditRequirement())) {
            // 不在合并区域但有选修学分要求，说明是单独的必选课程
            String groupCode = "EXC_SINGLE_" + tpId + "_" + version + "_" + data.getExcelRowIndex();
            data.setElectiveGroupCode(groupCode);
            data.setElectiveRequiredCredits(parseCredit(data.getElectiveCreditRequirement()));
        }
        // 3. 如果既不在合并区域也没有选修学分要求，则为必修课或不作学分要求的选修课，不分组
    }

    private Double parseCredit(String creditStr) {
        if (creditStr == null || creditStr.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(creditStr.trim());
        } catch (Exception e) {
            throw new ClientException("学分格式错误: " + creditStr);
        }
    }

}