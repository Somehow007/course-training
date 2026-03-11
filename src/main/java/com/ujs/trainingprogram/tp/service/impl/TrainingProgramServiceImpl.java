package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.constant.RedisKeyConstant;
import com.ujs.trainingprogram.tp.common.enums.CourseTypeEnum;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.dao.entity.*;
import com.ujs.trainingprogram.tp.dao.mapper.CollegeMapper;
import com.ujs.trainingprogram.tp.dao.mapper.CourseMapper;
import com.ujs.trainingprogram.tp.dao.mapper.MajorMapper;
import com.ujs.trainingprogram.tp.dao.mapper.TrainingProgramDetailMapper;
import com.ujs.trainingprogram.tp.dao.mapper.TrainingProgramMapper;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramAddCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramUpdateCourseReqDTO;
import com.ujs.trainingprogram.tp.dto.req.trainingprogram.TrainingProgramUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramDetailSelectRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramSelectRespDTO;
import com.ujs.trainingprogram.tp.excel.template.TrainingProgramExcelTemplate;
import com.ujs.trainingprogram.tp.service.CollegeService;
import com.ujs.trainingprogram.tp.service.CourseService;
import com.ujs.trainingprogram.tp.service.SysDictService;
import com.ujs.trainingprogram.tp.service.TrainingProgramService;
import com.ujs.trainingprogram.tp.utils.LoadCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import com.alibaba.excel.EasyExcel;
import com.ujs.trainingprogram.tp.excel.listener.ReadTrainingProgramListener;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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

    private static final String TP_NAME_SUFFIX = "%s专业课程设置及学时分配表";


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

            // 将数据转换为Excel DTO
            List<TrainingProgramExcelTemplate> excelDataList = dataList.stream()
                    .map(data -> {
                        TrainingProgramExcelTemplate excelDTO = new TrainingProgramExcelTemplate();
                        BeanUtil.copyProperties(data, excelDTO);
                        // 处理总学时字段的导出
                        buildTotalTime(data, excelDTO);
                        excelDTO.setElectiveCreditRequirement(data.getRequiredElective() != null ? data.getRequiredElective().toString() : "");
                        return excelDTO;
                    })
                    .toList();

            // 设置响应头
            String fileName = URLEncoder.encode(dataList.get(0).getName() + "专业设置及学时分配表.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            // 导出Excel
            EasyExcel.write(response.getOutputStream(), TrainingProgramExcelTemplate.class)
                    .sheet("培养计划")
                    .doWrite(excelDataList);
        } catch (IOException e) {
            throw new ClientException("导出Excel失败: " + e.getMessage());
        }
    }

    @Override
    public void importTrainingProgramFromExcel(MultipartFile file, String collegeId, String majorId) {
        try {
            // 1. 首先查询数据库，找到要导入的培养计划
            collegeId = removeLeadingComma(collegeId);
            majorId = removeLeadingComma(majorId);
            TrainingProgramSelectRespDTO trainingProgramSelectRespDTO = selectTrainingProgramByCollegeAndMajor(collegeId, majorId);

            Long tpId;
            int version;
            MajorDO majorDO = majorMapper.selectById(majorId);
            if (Objects.isNull(majorDO)) {
                log.warn("导入失败，该专业不存在: " + majorId);
                throw new ClientException("导入失败，该专业不存在。");
            }
            String fileName = String.format(majorDO.getMajorName(), "专业课程设置及学时分配表");
            if (trainingProgramSelectRespDTO == null
                    || trainingProgramSelectRespDTO.getId() == null
                    || trainingProgramSelectRespDTO.getId() == 0L) {
                // 不存在，就新创建
                TrainingProgramDO newTrainingProgram = TrainingProgramDO.builder()
                        .name(fileName)
                        .collegeId(Long.parseLong(collegeId))
                        .majorId(Long.parseLong(majorId))
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

            // 3. 检查缓存，若没有，则预加载数据到缓存中，避免逐条查询数据库
            loadCacheUtils.loadCollegeCache();
            loadCacheUtils.loadSysDictCache();
            loadCacheUtils.loadCourseCache();

            // 3. 使用自定义监听器读取Excel数据
            ReadTrainingProgramListener listener = new ReadTrainingProgramListener(this, Long.parseLong(majorId), tpId, version);
            EasyExcel.read(file.getInputStream(), TrainingProgramExcelTemplate.class, listener)
                    .sheet()
                    .doRead();

        } catch (IOException e) {
            throw new ClientException("导入Excel失败: " + e.getMessage());
        }
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
    public void batchSaveTrainingProgramDetails(List<TrainingProgramExcelTemplate> dataList, Long tpId, Long majorId, Integer version) {
        for (TrainingProgramExcelTemplate data : dataList) {
            try {

                // 将Excel DTO转换为数据库实体
                TrainingProgramDetailDO detailDO = TrainingProgramDetailDO.builder()
                        .id(IdUtil.getSnowflakeNextId())
                        .trainingProgramId(tpId)
                        // todo 缓存中如果不存在，则需要单独拎出来查数据库（可以考虑布隆过滤器过滤），通知用户
                        // todo 有些数据，比如课程不存在，需要告知用户是否添加进数据库？
                        .collegeId(Long.parseLong(Objects.requireNonNull(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisKeyConstant.COLLEGE_ID_NAME_CACHE_KEY, data.getCollegeName())).toString())))
                        .majorId(majorId)
                        .courseId(Long.parseLong(Objects.requireNonNull(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisKeyConstant.COURSE_NAME_ID_KEY, data.getCourseName())).toString())))
                        .courseName(data.getCourseName())
                        .courseNature(data.getCourseNature())
                        .term(data.getTerm())
                        .hourOutside(data.getHourOutside())
                        .hourPractice(data.getHourPractice())
                        .hourTeach(data.getHourTeach())
                        .hourWeek(data.getHourWeek())
                        .hourOperation(data.getHourOperation())
                        .remark(data.getRemark())
                        .requiredElective(data.getRequiredElective())
                        .totalCredits(data.getTotalCredits())
                        .version(version)
                        .build();

                // 处理总学时字段
                // 保存到数据库
                trainingProgramDetailMapper.insert(parseTimeUnits(detailDO, data.getTotalHours()));
            } catch (Exception e) {
                log.error("处理Excel记录时发生错误: {}", e.getMessage(), e);
                throw new ClientException("处理Excel记录时发生错误: " + e.getMessage());
            }
        }
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

}