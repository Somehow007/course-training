package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.constant.RedisKeyConstant;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.dao.entity.*;
import com.ujs.trainingprogram.tp.dao.mapper.CourseMapper;
import com.ujs.trainingprogram.tp.dao.mapper.MajorMapper;
import com.ujs.trainingprogram.tp.dao.mapper.TrainingProgramDetailMapper;
import com.ujs.trainingprogram.tp.dao.mapper.TrainingProgramMapper;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivityAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.courseexclusivity.CourseExclusivitySaveReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.*;
import com.ujs.trainingprogram.tp.dto.resp.courseexclusivity.CourseToExclusivityRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramDetailSelectRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramSelectRespDTO;
import com.ujs.trainingprogram.tp.excel.model.ExcelMergeRegion;
import com.ujs.trainingprogram.tp.excel.template.TrainingProgramExcelTemplate;
import com.ujs.trainingprogram.tp.service.*;
import com.ujs.trainingprogram.tp.utils.ExcelExportUtils;
import com.ujs.trainingprogram.tp.utils.LoadCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import com.alibaba.excel.EasyExcel;
import com.ujs.trainingprogram.tp.excel.listener.ReadTrainingProgramListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 培养计划业务逻辑实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingProgramServiceImpl extends ServiceImpl<TrainingProgramMapper, TrainingProgramDO> implements TrainingProgramService {

    private final TrainingProgramMapper trainingProgramMapper;
    private final CourseMapper courseMapper;
    private final TrainingProgramDetailMapper trainingProgramDetailMapper;
    private final MajorMapper majorMapper;
    private final LoadCacheUtils loadCacheUtils;
    private final StringRedisTemplate stringRedisTemplate;
    private final SysDictService sysDictService;

    private static final String TP_NAME_SUFFIX = "%s专业课程设置及学时分配表";
    private final CourseExclusivityService courseExclusivityService;
    private final TransactionTemplate transactionTemplate;


    @Override
    public void createTrainingProgram(TrainingProgramCreateReqDTO requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new ClientException("创建培养计划失败，请确认传入的参数有效");
        }
        TrainingProgramDO trainingProgramDO = BeanUtil.toBean(requestParam, TrainingProgramDO.class);
        MajorDO majorDO = majorMapper.selectById(requestParam.getMajorId());
        trainingProgramDO.setId(IdUtil.getSnowflakeNextId());
        trainingProgramDO.setName(String.format(majorDO.getMajorName(), TP_NAME_SUFFIX));
        baseMapper.insert(trainingProgramDO);
    }

    @Override
    public Long createTrainingProgram(TrainingProgramDO requestParam) {
        if (Objects.isNull(requestParam)) {
            throw new ClientException("创建培养计划失败，请确认传入的参数有效");
        }
        Long id = IdUtil.getSnowflakeNextId();
        requestParam.setId(id);
        baseMapper.insert(requestParam);
        return id;
    }

    @Override
    public void addCourseToTrainingProgram(TrainingProgramAddCourseReqDTO requestParam) {
        if (StrUtil.isBlank(requestParam.getCourseId()) || StrUtil.isBlank(requestParam.getTrainingProgramId())) {
            throw new ClientException("添加课程至培养计划失败，请传入待添加的培养计划与课程");
        }
        TrainingProgramDO trainingProgramDO = trainingProgramMapper.selectById(requestParam.getTrainingProgramId());
        if (Objects.isNull(trainingProgramDO)) {
            throw new ClientException("添加课程至培养计划失败，请传入待添加的培养计划");
        }
        CourseDO courseDO = courseMapper.selectById(requestParam.getCourseId());
        if (Objects.isNull(courseDO)) {
            throw new ClientException("添加课程至培养计划失败，请传入待添加的课程");
        }

        TrainingProgramDetailDO trainingProgramDetailDO = BeanUtil.toBean(requestParam, TrainingProgramDetailDO.class);
        trainingProgramDetailDO.setId(IdUtil.getSnowflakeNextId());
        trainingProgramDetailDO.setCourseNature(courseDO.getCourseNature());
        trainingProgramDetailDO.setCourseName(courseDO.getCourseName());

        trainingProgramDetailMapper.insert(trainingProgramDetailDO);

        if (StrUtil.isNotBlank(String.valueOf(requestParam.getMajorId()))) {
            majorMapper.incrementCourseNum(Long.parseLong(requestParam.getMajorId()), 1);
        }
    }

    @Override
    public void updateCourseToTrainingProgram(TrainingProgramUpdateCourseReqDTO requestParam) {
        LambdaQueryWrapper<TrainingProgramDetailDO> queryWrapper = Wrappers.lambdaQuery(TrainingProgramDetailDO.class)
                .eq(TrainingProgramDetailDO::getId, requestParam.getId())
                .eq(TrainingProgramDetailDO::getDelFlag, 0);
        TrainingProgramDetailDO originalTrainingProgramDetailDO = trainingProgramDetailMapper.selectOne(queryWrapper);
        if (Objects.isNull(originalTrainingProgramDetailDO)) {
            throw new ClientException("修改培养计划课程失败，请传入待修改的培养计划课程");
        }

        TrainingProgramDetailDO trainingProgramDetailDO = BeanUtil.toBean(originalTrainingProgramDetailDO, TrainingProgramDetailDO.class);
        BeanUtil.copyProperties(requestParam, trainingProgramDetailDO);
        validateTimeUnits(trainingProgramDetailDO);

        // 如果专业更改了修改专业中课程
        if (!Objects.equals(requestParam.getMajorId(), String.valueOf(originalTrainingProgramDetailDO.getMajorId())) &&
                StrUtil.isNotBlank(String.valueOf(requestParam.getMajorId()))) {
            int isDecrementSuccess = majorMapper.decrementCourseNum(originalTrainingProgramDetailDO.getMajorId(), 1);
            int isIncrementSuccess = majorMapper.incrementCourseNum(Long.parseLong(requestParam.getMajorId()), 1);
            if (isIncrementSuccess <= 0 || isDecrementSuccess <= 0) {
                throw new ClientException("课程信息更新失败：专业课程数量修改失败");
            }
        }
    }

    /**
     * 组装学时
     *
     * @param requestParam  培养计划单条记录
     */
    private void validateTimeUnits(TrainingProgramDetailDO requestParam) {
        boolean hasTotalHours = Optional.ofNullable(requestParam.getTotalHours()).orElse(0f) > 0;
        boolean hasTotalWeeks = Optional.ofNullable(requestParam.getTotalWeeks()).orElse(0f) > 0;
        if (hasTotalWeeks == hasTotalHours) {
            throw new ClientException("创建课程失败，totalHours 和 totalWeeks 必须有且只有一个有值");
        }

        if (hasTotalHours) {
            requestParam.setHoursUnit(0);
        }
        if (hasTotalWeeks) {
            requestParam.setHoursUnit(1);
        }
    }

    @Override
    public void deleteTrainingProgram(String id) {
        LambdaUpdateWrapper<TrainingProgramDO> updateWrapper = Wrappers.lambdaUpdate(TrainingProgramDO.class)
                .eq(TrainingProgramDO::getId, id)
                .eq(TrainingProgramDO::getDelFlag, 0)
                .set(TrainingProgramDO::getDelFlag, 1);
        trainingProgramMapper.update(updateWrapper);
    }

    @Override
    public void deleteTrainingProgramDetails(String id) {
        LambdaUpdateWrapper<TrainingProgramDetailDO> updateWrapper = Wrappers.lambdaUpdate(TrainingProgramDetailDO.class)
                .eq(TrainingProgramDetailDO::getDelFlag, 0)
                .eq(TrainingProgramDetailDO::getTrainingProgramId, Long.parseLong(id))
                .set(TrainingProgramDetailDO::getDelFlag, 1);
        trainingProgramDetailMapper.update(updateWrapper);
    }

    @Override
    public void updateTrainingProgram(TrainingProgramUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<TrainingProgramDO> updateWrapper = Wrappers.lambdaUpdate(TrainingProgramDO.class)
                .eq(TrainingProgramDO::getId, requestParam.getId())
                .set(StrUtil.isNotBlank(requestParam.getName()), TrainingProgramDO::getName, requestParam.getName())
                .set(StrUtil.isNotBlank(requestParam.getYear()), TrainingProgramDO::getYear, requestParam.getYear())
                .set(StrUtil.isNotBlank(requestParam.getDescription()), TrainingProgramDO::getDescription, requestParam.getDescription());
        trainingProgramMapper.update(updateWrapper);
    }

    @Override
    public List<TrainingProgramDetailSelectRespDTO> selectTrainingProgramDetail(String id) {
        if (Objects.isNull(id)) {
            throw new ClientException("查询培养计划信息失败，请检查是否传入信息");
        }
        
        // 验证ID是否为有效的数字格式且在Long范围内
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ClientException("查询培养计划信息失败，传入的ID格式不正确");
        }
        
        return trainingProgramDetailMapper.selectTrainingProgramDetail(Long.parseLong(id));
    }

    @Override
    public void exportTrainingProgramToExcel(String id, HttpServletResponse response) {
        try {
            // 获取培养计划数据
            List<TrainingProgramDetailSelectRespDTO> dataList = selectTrainingProgramDetail(id);
            // 获取分组数据
            Map<String, CourseToExclusivityRespDTO> courseToExclusivityMap = courseExclusivityService.selectCourseToExclusivity(id);

            // 将数据转换为Excel DTO
            List<TrainingProgramExcelTemplate> excelDataList = new ArrayList<>(dataList.stream()
                    .map(data -> {
                        TrainingProgramExcelTemplate excelDTO = new TrainingProgramExcelTemplate();
                        BeanUtil.copyProperties(data, excelDTO);
                        // 处理总学时字段的导出
                        buildTotalTime(data, excelDTO);
                        excelDTO.setElectiveCreditRequirement(data.getRequiredElective() != null ? data.getRequiredElective().toString() : "");
                        excelDTO.setElectiveGroupCode(Objects.isNull(courseToExclusivityMap.get(data.getCourseName())) ? "" : courseToExclusivityMap.get(data.getCourseName()).getGroupCode());
                        return excelDTO;
                    })
                    .toList());

            // 1. 定义course_type的自定义排序顺序
            Map<String, Integer> courseTypeSortMap = sysDictService.listCourseTypeSysDict()
                    .stream()
                    .collect(Collectors.toMap(
                            SysDictDO::getDictName,
                            SysDictDO::getSortOrder,
                            (existing, replacement) -> existing // 处理重复键
                    ));

            // 升序
            excelDataList.sort(
                    Comparator.<TrainingProgramExcelTemplate, Integer>comparing(
                                    dto -> courseTypeSortMap.getOrDefault(dto.getCourseType(), Integer.MAX_VALUE)
                            )
                            .thenComparing(
                                    TrainingProgramExcelTemplate::getCourseNature,
                                    Comparator.nullsLast(Comparator.naturalOrder())
                            )
                            .thenComparing(
                                    TrainingProgramExcelTemplate::getElectiveGroupCode,
                                    Comparator.nullsLast(Comparator.naturalOrder())
                            )
            );

            List<TrainingProgramExcelTemplate> finalList = ExcelExportUtils.insertSummaryRows(excelDataList);

            // 设置响应头
            String fileName = URLEncoder.encode(dataList.get(0).getName() + "专业设置及学时分配表.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // 导出Excel
            EasyExcel.write(baos, TrainingProgramExcelTemplate.class)
                    .sheet("培养计划")
                    .doWrite(finalList);

            // 合并单元格（第1列=0, 第2列=1, 第13列=12）
            byte[] mergedBytes = ExcelExportUtils.mergeConsecutiveSameCells(
                    baos.toByteArray(), 0, 0, 1, 12
            );

            // 输出
            response.getOutputStream().write(mergedBytes);
            response.getOutputStream().flush();

        } catch (IOException e) {
            throw new ClientException("导出Excel失败: " + e.getMessage());
        }
    }

    @Override
    public void importTrainingProgramFromExcel(MultipartFile file, String collegeId, String majorId) {

        // 1. 检查缓存，若没有，则预加载数据到缓存中，避免逐条查询数据库
        loadCacheUtils.loadCollegeCache();
        loadCacheUtils.loadSysDictCache();
        loadCacheUtils.loadCourseCache();

        // 从缓存获取学院 ID
        collegeId = removeLeadingComma(collegeId);
        majorId = removeLeadingComma(majorId);
        List<Object> collegeValueStr = stringRedisTemplate.opsForHash()
                .values(RedisKeyConstant.COLLEGE_ID_NAME_CACHE_KEY);
        if (!collegeValueStr.contains(collegeId)) {
            throw new ClientException("学院不存在：" + collegeId + "，请先添加该学院");
        }

        // 2. 首先查询数据库，找到要导入的培养计划
        TrainingProgramSelectRespDTO trainingProgramSelectRespDTO = selectTrainingProgramByCollegeAndMajor(collegeId, majorId);


        String finalCollegeId = collegeId;
        String finalMajorId = majorId;
        transactionTemplate.executeWithoutResult(status -> {

            Long tpId;
            int version;
            MajorDO majorDO = majorMapper.selectById(finalMajorId);
            if (Objects.isNull(majorDO)) {
                log.warn("导入失败，该专业不存在: " + finalMajorId);
                throw new ClientException("导入失败，该专业不存在。");
            }
            String fileName = String.format(majorDO.getMajorName(), "专业课程设置及学时分配表");
            try {
                if (trainingProgramSelectRespDTO == null
                        || trainingProgramSelectRespDTO.getId() == null
                        || trainingProgramSelectRespDTO.getId() == 0L) {
                    // 不存在，就新创建
                    TrainingProgramDO newTrainingProgram = TrainingProgramDO.builder()
                            .name(fileName)
                            .collegeId(Long.parseLong(finalCollegeId))
                            .majorId(Long.parseLong(finalMajorId))
                            .year(LocalDateTime.now().getYear())
                            .build();

                    // 获取培养计划Id，后续直接组培养计划表就行
                    tpId = createTrainingProgram(newTrainingProgram);
                    version = 1;
                } else {
                    // 如果存在，获取已有的培养计划 ID，并获取其当前版本号
                    tpId = trainingProgramSelectRespDTO.getId();
                    List<TrainingProgramDetailSelectRespDTO> originData = selectTrainingProgramDetail(tpId.toString());
                    if (CollUtil.isEmpty(originData) || originData.get(0).getVersion() == null) {
                        version = 1;
                    } else {
                        // 2. 将原培养计划的详细内容全部软删除，并更新版本；后续导入 Excel 中的版本
                        deleteTrainingProgramDetails(tpId.toString());
                        version = originData.get(0).getVersion() != null ? originData.get(0).getVersion() + 1 : 1;
                    }
                }

                // 预读合并区域信息
                Map<Integer, ExcelMergeRegion> mergeRegionMap = readMergeRegions(file);

                // 3. 使用自定义监听器读取Excel数据
                ReadTrainingProgramListener listener = new ReadTrainingProgramListener(
                        this,
                        Long.parseLong(finalMajorId),
                        tpId,
                        version,
                        mergeRegionMap);
                EasyExcel.read(file.getInputStream(), TrainingProgramExcelTemplate.class, listener)
                        .sheet()
                        .doRead();

            } catch (Exception ex) {
                status.setRollbackOnly();
                if (ex instanceof IOException) {
                    throw new ClientException("导入文件失败");
                }
                if (ex instanceof ClientException) {
                    throw (ClientException) ex;
                }
                throw new ClientException("导入 Excel 失败：" + ex.getMessage());
            }
        });

    }
    
    @Override
    public void downloadTrainingProgramTemplate(HttpServletResponse response) {
        try {
            // 设置响应头
            String fileName = URLEncoder.encode("培养计划导入模板.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            // 生成只有表头的Excel模板
            EasyExcel.write(response.getOutputStream(), TrainingProgramExcelTemplate.class)
                    .sheet("培养计划模板")
                    .doWrite(List.of()); // 空数据列表，只包含表头
        } catch (IOException e) {
            throw new ClientException("下载模板失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveTrainingProgramDetailsFromExcel(List<TrainingProgramExcelTemplate> dataList, Long tpId, Long majorId, Integer version) {

        // 检查数据中的课程与学院是否都存在
        judgeCourseAndCourseIsExist(dataList);
        // 分组的课程和没有分组的课程
        List<TrainingProgramExcelTemplate> groupRequiredList = new ArrayList<>();

        for (TrainingProgramExcelTemplate data : dataList) {
            if (StrUtil.isNotBlank(data.getElectiveCreditRequirement())) {
                groupRequiredList.add(data);
            }
        }

        if (CollUtil.isNotEmpty(groupRequiredList)) {
            handleGroupedCourses(groupRequiredList, tpId, version);
        }

        List<TrainingProgramAddCourseFromExcelReqDTO> requestParams = convertExcelToDTOs(dataList, tpId, majorId);
        batchSaveTrainingProgramDetails(requestParams, version);

    }

    @Override
    public void batchSaveTrainingProgramDetails(List<TrainingProgramAddCourseFromExcelReqDTO> requestParams, Integer version) {
        if (CollUtil.isEmpty(requestParams)) {
            throw new ClientException("批量添加失败：参数列表不能为空");
        }

        // 1. 提取所有的 trainingProgramId、courseId、collegeId、majorId
        Set<String> courseIds = new HashSet<>();

        for(TrainingProgramAddCourseFromExcelReqDTO param : requestParams) {
            if (StrUtil.isBlank(param.getTrainingProgramId()) || StrUtil.isBlank(param.getCourseId().toString())) {
                throw new ClientException("批量添加失败：培养计划 ID 或课程 ID 为空");
            }
            courseIds.add(param.getCourseId().toString());
        }

        // 2. 批量查询，验证课程是否存在
        List<CourseDO> courses = courseMapper.selectBatchIds(courseIds);
        if (courses.size() != courseIds.size()) {
            Set<String> existingIds = courses.stream()
                    .map(CourseDO::getId)
                    .map(String::valueOf)
                    .collect(Collectors.toSet());

            Set<String> notFoundIds = courseIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toSet());

            throw new ClientException("批量添加失败：以下课程不存在：" + String.join(", ", notFoundIds));
        }

        // 6. 转换为实体对象列表
        List<TrainingProgramDetailDO> details = requestParams.stream()
                .map(param -> {
//                    CourseDO course = courseMap.get(param.getCourseId());
//                    if (course == null) {
//                        throw new ClientException("课程信息未找到：" + param.getCourseId());
//                    }

                    TrainingProgramDetailDO detailDO = TrainingProgramDetailDO.builder()
                            .id(IdUtil.getSnowflakeNextId())
                            .trainingProgramId(Long.parseLong(param.getTrainingProgramId()))
                            .courseId(param.getCourseId())
                            .courseNature(param.getCourseNature())  // 从课程中获取课程性质todo 待优化，对已存在的数据进行比对
                            .courseName(param.getCourseName())      // 从课程中获取课程名称
                            .collegeId(param.getCollegeId())
                            .majorId(param.getMajorId())
                            .totalCredits(param.getTotalCredits())
                            .hourTeach(param.getHourTeach())
                            .hourPractice(param.getHourPractice())
                            .hourOperation(param.getHourOperation())
                            .hourOutside(param.getHourOutside())
                            .hourWeek(param.getHourWeek())
                            .requiredElective(param.getRequiredElective())
                            .term(param.getTerm())
                            .remark(param.getRemark())
                            .version(version != null ? version : 1)
                            .build();
                    return parseTimeUnits(detailDO, param.getTotalHours());
                })
                .collect(Collectors.toList());

        // 7. 批量插入（使用 MyBatis 原生批量插入）
        int successCount = trainingProgramDetailMapper.insertBatch(details);

        if (successCount <= 0) {
            throw new ClientException("批量添加课程至培养计划失败");
        }

        log.info("批量添加成功，共插入 {} 条记录", successCount);

    }


    @Override
    public TrainingProgramSelectRespDTO selectTrainingProgramByCollegeAndMajor(String collegeId, String majorId) {
        if (StrUtil.isBlank(collegeId) && StrUtil.isBlank(majorId)) {
            throw new ClientException("学院与专业参数为空，请检查后再次尝试。");
        }

        LambdaQueryWrapper<TrainingProgramDO> queryWrapper = Wrappers.lambdaQuery(TrainingProgramDO.class)
                .eq(TrainingProgramDO::getCollegeId, Long.parseLong(collegeId))
                .eq(TrainingProgramDO::getMajorId, Long.parseLong(majorId))
                .eq(TrainingProgramDO::getDelFlag, 0);
        TrainingProgramDO trainingProgramDO = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(trainingProgramDO)) {
            return null;
        }
        return BeanUtil.toBean(trainingProgramDO, TrainingProgramSelectRespDTO.class);
    }

    /**
     * 将 Excel 数据转换为 DTO 列表
     */
    private List<TrainingProgramAddCourseFromExcelReqDTO> convertExcelToDTOs(List<TrainingProgramExcelTemplate> excelData, Long tpId, Long majorId) {
        List<TrainingProgramAddCourseFromExcelReqDTO> requestParams = new ArrayList<>();


        for(TrainingProgramExcelTemplate data: excelData) {
            // 从缓存获取课程 ID
            String courseIdStr = (String) stringRedisTemplate.opsForHash()
                    .get(RedisKeyConstant.COURSE_NAME_ID_KEY, data.getCourseName());

            // 从缓存获取学院 ID
            String collegeIdStr = "";
            if (StrUtil.isNotBlank(data.getCollegeName())) {
                collegeIdStr = (String) stringRedisTemplate.opsForHash()
                        .get(RedisKeyConstant.COLLEGE_ID_NAME_CACHE_KEY, data.getCollegeName());
            }

            TrainingProgramAddCourseFromExcelReqDTO dto = TrainingProgramAddCourseFromExcelReqDTO.builder()
                    .trainingProgramId(tpId.toString())
                    .courseId(StrUtil.isNotBlank(courseIdStr) ? Long.parseLong(courseIdStr) : null)
                    .collegeId(StrUtil.isNotBlank(collegeIdStr) ? Long.parseLong(collegeIdStr) : null)
                    .majorId(majorId)
                    .courseName(data.getCourseName())
                    .courseNature(data.getCourseNature())
                    .totalCredits(data.getTotalCredits())
                    .hourTeach(data.getHourTeach())
                    .hourPractice(data.getHourPractice())
                    .hourOperation(data.getHourOperation())
                    .hourOutside(data.getHourOutside())
                    .hourWeek(data.getHourWeek())
                    .requiredElective(StrUtil.isNotBlank(data.getElectiveCreditRequirement()) ?
                            Integer.parseInt(data.getElectiveCreditRequirement()) : null)
                    .term(data.getTerm())
                    .remark(data.getRemark())
                    .totalHours(data.getTotalHours())
                    .build();

            requestParams.add(dto);
        }

        return requestParams;
    }

    /**
     * 处理分组课程
     */
    private void handleGroupedCourses(List<TrainingProgramExcelTemplate> groupRequiredList, Long tpId, Integer version) {

        // 按分组编码分组
        Map<String, List<TrainingProgramExcelTemplate>> groupMap = groupRequiredList.stream()
                .collect(Collectors.groupingBy(TrainingProgramExcelTemplate::getElectiveGroupCode));

        // 创建分组，先检查是否存在
        List<CourseExclusivityDO> courseExclusivityDOs = courseExclusivityService.selectByTpId(tpId.toString());

        if (CollUtil.isNotEmpty(courseExclusivityDOs)) {
            // 存在的话，将详情表全部软删除，并获取版本号
            List<String> exclusivityIds = courseExclusivityDOs.stream()
                    .map(each -> each.getId().toString())
                    .toList();
            courseExclusivityService.deleteCourseExclusivity(List.of(tpId.toString()));
            courseExclusivityService.deleteCourseExclusivityDetail(exclusivityIds);
        }

        for (Map.Entry<String, List<TrainingProgramExcelTemplate>> entry : groupMap.entrySet()) {
            String groupCode = entry.getKey();
            List<TrainingProgramExcelTemplate> items = entry.getValue();
            Double requiredCredits = items.get(0).getElectiveRequiredCredits();


            CourseExclusivitySaveReqDTO courseExclusivitySaveReqDTO = CourseExclusivitySaveReqDTO.builder()
                    .requiredCredits((int) Double.parseDouble(String.valueOf(requiredCredits)))
                    .groupCode(groupCode)
                    .trainingProgramId(tpId)
                    .version(version)
                    .build();

            // 保存详细的分组关联关系
            Long finalExclusivityId = courseExclusivityService.createCourseExclusivity(courseExclusivitySaveReqDTO);
            List<CourseExclusivityAddCourseReqDTO> details = items.stream()
                    .map(item -> CourseExclusivityAddCourseReqDTO.builder()
                            .exclusivityId(finalExclusivityId.toString())
                            .courseId(Objects.requireNonNull(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisKeyConstant.COURSE_NAME_ID_KEY, item.getCourseName())).toString()))
                            .build())
                    .toList();

            courseExclusivityService.batchAddCourseToCourseExclusivity(details);
        }
    }

    /**
     * 构建导出时的总学时字段
     * 
     * @param data 数据库查询结果
     * @param excelDTO Excel DTO对象
     */
    private void buildTotalTime(TrainingProgramDetailSelectRespDTO data, TrainingProgramExcelTemplate excelDTO) {
        if (data.getHoursUnit() != null) {
            if (data.getHoursUnit() == 1 && data.getTotalWeeks() != null) {
                // 周单位
                excelDTO.setTotalHours(data.getTotalWeeks() + "周");
            } else if (data.getHoursUnit() == 0 && data.getTotalHours() != null) {
                // 小时单位
                excelDTO.setTotalHours(String.valueOf(data.getTotalHours()));
            } else {
                excelDTO.setTotalHours("");
            }
        } else {
            excelDTO.setTotalHours("");
        }
    }

    /**
     * 安全地移除字符串开头的逗号（仅当第一个字符是 ',' 时）
     * 不影响正常字符串，避免误删内容中的逗号
     */
    public static String removeLeadingComma(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // 只有当第一个字符是逗号时才截取
        if (input.charAt(0) == ',') {
            return input.substring(1);
        }
        return input;
    }

    /**
     * 解析导入的模版学时
     *
     * @param requestParam  请求参数
     * @param totalTimeStr  模版中的总学时
     * @return  修改后的参数
     */
    private TrainingProgramDetailDO parseTimeUnits(TrainingProgramDetailDO requestParam, String totalTimeStr) {
        if (StrUtil.isBlank(totalTimeStr)) {
            return requestParam;
        }
        totalTimeStr = totalTimeStr.trim();

        Pattern pattern = Pattern.compile("^(\\d+(?:\\.\\d+)?)\\s*(周)?$");
        Matcher matcher = pattern.matcher(totalTimeStr);

        if (!matcher.matches()) {
            throw new ClientException("学时格式不正确，应为数字 + 周，例如: 30、1.5周，当前值为: " + totalTimeStr);
        }

        String numberStr = matcher.group(1);
        String unit = matcher.group(2);

        try {
            Float value = Float.parseFloat(numberStr);

            if ("周".equals(unit)) {
                requestParam.setTotalHours(null);
                requestParam.setHoursUnit(1);
                requestParam.setTotalWeeks(value);
            } else {
                requestParam.setTotalHours(value);
                requestParam.setHoursUnit(0);
                requestParam.setTotalWeeks(null);
            }
        } catch (NumberFormatException e) {
            throw new ClientException("数值解析失败: " + numberStr);
        }

        return requestParam;
    }

    /**
     * 读取合并区域
     */
    private Map<Integer, ExcelMergeRegion> readMergeRegions(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Map<Integer, ExcelMergeRegion> rowToRegion = new HashMap<>();
            final int CREDIT_COLUMN_INDEX = 12; // 选修要求学分列（0-based列索引，第6列）

            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                CellRangeAddress range = sheet.getMergedRegion(i);

                // 跳过前两行（0-based 行号 0 和 1 是表头）
                if (range.getLastRow() <= 1) {
                    continue; // 完全在表头区域，跳过
                }

                // 只处理包含"选修要求学分"列的合并区域
                if (range.getFirstColumn() <= CREDIT_COLUMN_INDEX &&
                        range.getLastColumn() >= CREDIT_COLUMN_INDEX) {

                    // 调整起始行：如果合并区域从表头开始，但延伸到数据区，则从第2行（0-based=2）开始
                    int actualFirstRow = Math.max(range.getFirstRow(), 2);
                    int actualLastRow = range.getLastRow();

                    // 确保调整后的区域有效
                    if (actualFirstRow <= actualLastRow) {
                        Row firstDataRow = sheet.getRow(actualFirstRow);
                        Cell cell = firstDataRow != null ? firstDataRow.getCell(CREDIT_COLUMN_INDEX) : null;
                        String value = getCellValueAsString(cell);

                        if (StrUtil.isNotBlank(value)) {
                            ExcelMergeRegion region = new ExcelMergeRegion();
                            region.setFirstRow(actualFirstRow);
                            region.setLastRow(actualLastRow);
                            region.setValue(value);

                            // 建立每行到合并区域的映射
                            for (int r = actualFirstRow; r <= actualLastRow; r++) {
                                rowToRegion.put(r, region);
                            }
                        }
                    }
                }
            }
            return rowToRegion;
        } catch (Exception e) {
            throw new ClientException("Excel合并区域解析失败: " + e.getMessage());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> null;
        };
    }

    private void judgeCourseAndCourseIsExist(List<TrainingProgramExcelTemplate> requestParam) {
        Set<String> courseFailConvert = new LinkedHashSet<>();
        Set<String> collegeFailConvert = new LinkedHashSet<>();

        for(TrainingProgramExcelTemplate data: requestParam) {
            // 从缓存获取课程 ID
            String courseIdStr = (String) stringRedisTemplate.opsForHash()
                    .get(RedisKeyConstant.COURSE_NAME_ID_KEY, data.getCourseName());
            if (StrUtil.isBlank(courseIdStr)) {
                // 告知用户哪些课程需要事先创建
                log.warn("课程不存在：{}，请先添加该课程", data.getCourseName());
                courseFailConvert.add("课程不存在：" + data.getCourseName() + "，请先添加该课程");
            }

            // 从缓存获取学院 ID
            if (StrUtil.isNotBlank(data.getCollegeName())) {
                String collegeIdStr = (String) stringRedisTemplate.opsForHash()
                        .get(RedisKeyConstant.COLLEGE_ID_NAME_CACHE_KEY, data.getCollegeName());
                if (StrUtil.isBlank(collegeIdStr)) {
                    // 告知用户哪些学院需要事先创建
                    log.warn("学院不存在：{}，请检查学院名称是否正确，若确实不存在，请告知管理员进行添加。", data.getCourseName());
                    collegeFailConvert.add("学院不存在：" + data.getCourseName() + "，，请检查学院名称是否正确，若确实不存在，请告知管理员进行添加。");
                }
            }
        }
        if (CollUtil.isNotEmpty(courseFailConvert) || CollUtil.isNotEmpty(collegeFailConvert)) {
            StringBuilder fullMessage = new StringBuilder();
            fullMessage.append("导入失败，请修正以下问题后重试：\n");

            if (!courseFailConvert.isEmpty()) {
                fullMessage.append("【课程缺失】\n");
                int index = 1;
                for (String error : courseFailConvert) {
                    fullMessage.append("  ").append(index++).append(". ").append(error).append("\n");
                }
            }

            if (!collegeFailConvert.isEmpty()) {
                fullMessage.append("【学院缺失】\n");
                int index = 1;
                for (String error : collegeFailConvert) {
                    fullMessage.append("  ").append(index++).append(". ").append(error).append("\n");
                }
            }
            throw new ClientException(fullMessage.toString().trim());
        }
    }

}