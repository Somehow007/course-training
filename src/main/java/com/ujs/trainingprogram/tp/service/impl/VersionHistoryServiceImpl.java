package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.enums.ChangeTypeEnum;
import com.ujs.trainingprogram.tp.common.enums.VersionStatusEnum;
import com.ujs.trainingprogram.tp.common.exception.ServiceException;
import com.ujs.trainingprogram.tp.dao.entity.*;
import com.ujs.trainingprogram.tp.dao.mapper.*;
import com.ujs.trainingprogram.tp.dto.req.version.*;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramDetailSelectRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.version.*;
import com.ujs.trainingprogram.tp.excel.template.TrainingProgramExcelTemplate;
import com.ujs.trainingprogram.tp.service.*;
import com.ujs.trainingprogram.tp.utils.ExcelExportUtils;
import com.ujs.trainingprogram.tp.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VersionHistoryServiceImpl extends ServiceImpl<VersionHistoryMapper, VersionHistoryDO> implements VersionHistoryService {

    private final VersionHistoryMapper versionHistoryMapper;
    private final VersionChangeLogMapper versionChangeLogMapper;
    private final TrainingProgramMapper trainingProgramMapper;
    private final TrainingProgramDetailMapper trainingProgramDetailMapper;
    private final TrainingProgramService trainingProgramService;
    private final CollegeMapper collegeMapper;
    private final MajorMapper majorMapper;
    private final CourseMapper courseMapper;
    private final SysDictMapper sysDictMapper;

    private static final Set<String> EXCLUDED_COMPARE_FIELDS = Set.of(
            "id", "trainingProgramId", "serialVersionUID", "createTime", "updateTime", "delFlag", "version"
    );

    private static final Map<String, String> FIELD_NAME_MAPPING = Map.ofEntries(
            Map.entry("courseNature", "课程性质"),
            Map.entry("courseName", "课程名称"),
            Map.entry("collegeId", "开课学院"),
            Map.entry("majorId", "修读专业"),
            Map.entry("totalCredits", "总学分"),
            Map.entry("totalHours", "总学时"),
            Map.entry("totalWeeks", "总学时(周)"),
            Map.entry("hoursUnit", "学时单位"),
            Map.entry("hourTeach", "授课学时"),
            Map.entry("hourPractice", "实验学时"),
            Map.entry("hourOperation", "上机学时"),
            Map.entry("hourOutside", "实践学时"),
            Map.entry("hourWeek", "周学时"),
            Map.entry("requiredElective", "选修学分要求"),
            Map.entry("term", "建议修读学期"),
            Map.entry("remark", "备注")
    );

    @Override
    public IPage<VersionListRespDTO> pageVersionHistory(VersionPageReqDTO requestParam) {
        Page<VersionHistoryDO> page = new Page<>(requestParam.getCurrent(), requestParam.getSize());

        LambdaQueryWrapper<VersionHistoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VersionHistoryDO::getDelFlag, 0)
               .eq(requestParam.getTrainingProgramId() != null && !requestParam.getTrainingProgramId().isEmpty(),
                   VersionHistoryDO::getTrainingProgramId, requestParam.getTrainingProgramId())
               .eq(requestParam.getVersionStatus() != null, VersionHistoryDO::getVersionStatus, requestParam.getVersionStatus())
               .like(requestParam.getVersionName() != null && !requestParam.getVersionName().isEmpty(),
                     VersionHistoryDO::getVersionName, requestParam.getVersionName())
               .like(requestParam.getCreatorName() != null && !requestParam.getCreatorName().isEmpty(),
                     VersionHistoryDO::getCreatorName, requestParam.getCreatorName())
               .orderByDesc(VersionHistoryDO::getCreateTime);

        IPage<VersionHistoryDO> versionPage = versionHistoryMapper.selectPage(page, wrapper);

        return versionPage.convert(this::convertToListRespDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createVersion(VersionCreateReqDTO requestParam) {
        Long trainingProgramId = Long.parseLong(requestParam.getTrainingProgramId());

        TrainingProgramDO trainingProgram = trainingProgramMapper.selectById(trainingProgramId);
        if (trainingProgram == null) {
            throw new ServiceException("培养方案不存在");
        }

        Integer currentVersion = trainingProgram.getCurrentVersion() != null ? trainingProgram.getCurrentVersion() : 0;
        Integer newVersionNumber = currentVersion + 1;

        List<TrainingProgramDetailDO> details = trainingProgramService.selectTrainingProgramDetailDOs(trainingProgramId.toString());
        String snapshotData = JSON.toJSONString(details);

        Long userId = SecurityUtils.getCurrentUserId();
        String userName = SecurityUtils.getCurrentUsername();

        VersionHistoryDO versionHistory = VersionHistoryDO.builder()
                .id(IdUtil.getSnowflakeNextId())
                .trainingProgramId(trainingProgramId)
                .versionNumber(newVersionNumber)
                .versionName(requestParam.getVersionName())
                .versionStatus(VersionStatusEnum.DRAFT.getCode())
                .changeDescription(requestParam.getChangeDescription())
                .snapshotData(snapshotData)
                .creatorId(userId)
                .creatorName(userName)
                .build();

        versionHistoryMapper.insert(versionHistory);

        trainingProgram.setCurrentVersion(newVersionNumber);
        trainingProgram.setLastVersionId(versionHistory.getId());
        trainingProgramMapper.updateById(trainingProgram);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VersionHistoryDO createVersionFromImport(Long trainingProgramId, String changeDescription, boolean isFirstVersion) {
        TrainingProgramDO trainingProgram = trainingProgramMapper.selectById(trainingProgramId);
        if (trainingProgram == null) {
            throw new ServiceException("培养方案不存在");
        }

        List<TrainingProgramDetailDO> oldDetails = isFirstVersion
                ? Collections.emptyList()
                : trainingProgramService.selectTrainingProgramDetailDOs(trainingProgramId.toString());
        String oldSnapshot = JSON.toJSONString(oldDetails);

        Integer currentVersion = trainingProgram.getCurrentVersion() != null ? trainingProgram.getCurrentVersion() : 0;
        Integer newVersionNumber = currentVersion + 1;

        Long userId;
        String userName;
        try {
            userId = SecurityUtils.getCurrentUserId();
            userName = SecurityUtils.getCurrentUsername();
        } catch (Exception e) {
            userId = 0L;
            userName = "系统";
        }

        String versionName = isFirstVersion
                ? trainingProgram.getName() + " - 初始版本"
                : trainingProgram.getName() + " - V" + newVersionNumber;

        Integer versionStatus = isFirstVersion
                ? VersionStatusEnum.PUBLISHED.getCode()
                : VersionStatusEnum.PUBLISHED.getCode();

        VersionHistoryDO oldVersionHistory = null;
        if (!isFirstVersion && currentVersion > 0) {
            LambdaQueryWrapper<VersionHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VersionHistoryDO::getTrainingProgramId, trainingProgramId)
                       .eq(VersionHistoryDO::getVersionNumber, currentVersion)
                       .eq(VersionHistoryDO::getDelFlag, 0)
                       .orderByDesc(VersionHistoryDO::getCreateTime)
                       .last("LIMIT 1");
            oldVersionHistory = versionHistoryMapper.selectOne(queryWrapper);
        }

        VersionHistoryDO versionHistory = VersionHistoryDO.builder()
                .id(IdUtil.getSnowflakeNextId())
                .trainingProgramId(trainingProgramId)
                .versionNumber(newVersionNumber)
                .versionName(versionName)
                .versionStatus(versionStatus)
                .changeDescription(changeDescription != null ? changeDescription : (isFirstVersion ? "Excel导入创建初始版本" : "Excel导入更新版本"))
                .snapshotData(null)
                .creatorId(userId)
                .creatorName(userName)
                .publishTime(LocalDateTime.now())
                .publishUserId(userId)
                .publishUserName(userName)
                .build();

        versionHistoryMapper.insert(versionHistory);

        if (!isFirstVersion && oldVersionHistory != null && oldVersionHistory.getSnapshotData() == null) {
            oldVersionHistory.setSnapshotData(oldSnapshot);
            versionHistoryMapper.updateById(oldVersionHistory);
        }

        trainingProgram.setCurrentVersion(newVersionNumber);
        trainingProgram.setLastVersionId(versionHistory.getId());
        trainingProgram.setVersionStatus(VersionStatusEnum.PUBLISHED.getCode());
        trainingProgramMapper.updateById(trainingProgram);

        return versionHistory;
    }

    @Override
    public List<VersionListRespDTO> listVersionsByTrainingProgramId(String trainingProgramId) {
        LambdaQueryWrapper<VersionHistoryDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VersionHistoryDO::getTrainingProgramId, Long.parseLong(trainingProgramId))
               .eq(VersionHistoryDO::getDelFlag, 0)
               .orderByDesc(VersionHistoryDO::getCreateTime);

        List<VersionHistoryDO> versions = versionHistoryMapper.selectList(wrapper);
        return versions.stream().map(this::convertToListRespDTO).collect(Collectors.toList());
    }

    @Override
    public List<VersionChangeLogDO> getVersionChangeLogs(String versionId) {
        LambdaQueryWrapper<VersionChangeLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VersionChangeLogDO::getVersionId, Long.parseLong(versionId))
               .orderByAsc(VersionChangeLogDO::getCreateTime);
        return versionChangeLogMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordChangeLogs(Long versionId, List<TrainingProgramDetailDO> oldDetails, List<TrainingProgramDetailDO> newDetails) {
        if (oldDetails == null) oldDetails = new ArrayList<>();
        if (newDetails == null) newDetails = new ArrayList<>();

        Map<Long, TrainingProgramDetailDO> oldMap = oldDetails.stream()
                .collect(Collectors.toMap(TrainingProgramDetailDO::getCourseId, d -> d, (a, b) -> a));
        Map<Long, TrainingProgramDetailDO> newMap = newDetails.stream()
                .collect(Collectors.toMap(TrainingProgramDetailDO::getCourseId, d -> d, (a, b) -> a));

        for (TrainingProgramDetailDO newDetail : newMap.values()) {
            if (!oldMap.containsKey(newDetail.getCourseId())) {
                VersionChangeLogDO changeLog = VersionChangeLogDO.builder()
                        .id(IdUtil.getSnowflakeNextId())
                        .versionId(versionId)
                        .changeType(ChangeTypeEnum.ADD.getCode())
                        .changeTarget("detail")
                        .targetId(newDetail.getCourseId())
                        .oldValue(null)
                        .newValue(JSON.toJSONString(newDetail))
                        .changeDescription("新增课程：" + newDetail.getCourseName())
                        .build();
                versionChangeLogMapper.insert(changeLog);
            }
        }

        for (TrainingProgramDetailDO oldDetail : oldMap.values()) {
            if (!newMap.containsKey(oldDetail.getCourseId())) {
                VersionChangeLogDO changeLog = VersionChangeLogDO.builder()
                        .id(IdUtil.getSnowflakeNextId())
                        .versionId(versionId)
                        .changeType(ChangeTypeEnum.DELETE.getCode())
                        .changeTarget("detail")
                        .targetId(oldDetail.getCourseId())
                        .oldValue(JSON.toJSONString(oldDetail))
                        .newValue(null)
                        .changeDescription("删除课程：" + oldDetail.getCourseName())
                        .build();
                versionChangeLogMapper.insert(changeLog);
            } else {
                TrainingProgramDetailDO newDetail = newMap.get(oldDetail.getCourseId());
                List<String> changedFields = compareDetailFields(oldDetail, newDetail);
                if (!changedFields.isEmpty()) {
                    VersionChangeLogDO changeLog = VersionChangeLogDO.builder()
                            .id(IdUtil.getSnowflakeNextId())
                            .versionId(versionId)
                            .changeType(ChangeTypeEnum.UPDATE.getCode())
                            .changeTarget("detail")
                            .targetId(oldDetail.getCourseId())
                            .oldValue(JSON.toJSONString(oldDetail))
                            .newValue(JSON.toJSONString(newDetail))
                            .changeDescription("修改课程【" + oldDetail.getCourseName() + "】：" + String.join("、", changedFields))
                            .build();
                    versionChangeLogMapper.insert(changeLog);
                }
            }
        }
    }

    private List<String> compareDetailFields(TrainingProgramDetailDO oldDetail, TrainingProgramDetailDO newDetail) {
        List<String> changedFields = new ArrayList<>();
        Field[] fields = TrainingProgramDetailDO.class.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (EXCLUDED_COMPARE_FIELDS.contains(fieldName)) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object oldValue = field.get(oldDetail);
                Object newValue = field.get(newDetail);

                if (!Objects.equals(oldValue, newValue)) {
                    String displayName = FIELD_NAME_MAPPING.getOrDefault(fieldName, fieldName);
                    changedFields.add(displayName);
                }
            } catch (IllegalAccessException e) {
                log.warn("Failed to compare field: {}", fieldName);
            }
        }

        return changedFields;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishVersion(String versionId) {
        VersionHistoryDO version = versionHistoryMapper.selectById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }

        if (!VersionStatusEnum.DRAFT.getCode().equals(version.getVersionStatus())) {
            throw new ServiceException("只有草稿状态的版本才能发布");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        String userName = SecurityUtils.getCurrentUsername();

        version.setVersionStatus(VersionStatusEnum.PUBLISHED.getCode());
        version.setPublishTime(LocalDateTime.now());
        version.setPublishUserId(userId);
        version.setPublishUserName(userName);
        versionHistoryMapper.updateById(version);

        TrainingProgramDO trainingProgram = trainingProgramMapper.selectById(version.getTrainingProgramId());
        if (trainingProgram != null) {
            trainingProgram.setVersionStatus(VersionStatusEnum.PUBLISHED.getCode());
            trainingProgramMapper.updateById(trainingProgram);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackVersion(String versionId) {
        VersionHistoryDO version = versionHistoryMapper.selectById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }

        Long trainingProgramId = version.getTrainingProgramId();

        TrainingProgramDO trainingProgram = trainingProgramMapper.selectById(trainingProgramId);
        if (trainingProgram == null) {
            throw new ServiceException("培养方案不存在");
        }

        List<TrainingProgramDetailDO> currentDetails = trainingProgramService.selectTrainingProgramDetailDOs(trainingProgramId.toString());
        String currentSnapshot = JSON.toJSONString(currentDetails);

        Long userId = SecurityUtils.getCurrentUserId();
        String userName = SecurityUtils.getCurrentUsername();

        VersionHistoryDO rollbackVersion = VersionHistoryDO.builder()
                .id(IdUtil.getSnowflakeNextId())
                .trainingProgramId(trainingProgramId)
                .versionNumber(trainingProgram.getCurrentVersion() + 1)
                .versionName("回滚至版本 " + version.getVersionNumber())
                .versionStatus(VersionStatusEnum.ROLLED_BACK.getCode())
                .changeDescription("回滚操作：从版本 " + trainingProgram.getCurrentVersion() + " 回滚到版本 " + version.getVersionNumber())
                .snapshotData(currentSnapshot)
                .creatorId(userId)
                .creatorName(userName)
                .build();

        versionHistoryMapper.insert(rollbackVersion);

        List<TrainingProgramDetailDO> targetDetails = JSON.parseArray(version.getSnapshotData(), TrainingProgramDetailDO.class);

        LambdaQueryWrapper<TrainingProgramDetailDO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(TrainingProgramDetailDO::getTrainingProgramId, trainingProgramId);
        trainingProgramDetailMapper.delete(deleteWrapper);

        if (targetDetails != null && !targetDetails.isEmpty()) {
            for (TrainingProgramDetailDO detail : targetDetails) {
                TrainingProgramDetailDO newDetail = TrainingProgramDetailDO.builder()
                        .id(IdUtil.getSnowflakeNextId())
                        .trainingProgramId(trainingProgramId)
                        .courseId(detail.getCourseId())
                        .courseNature(detail.getCourseNature())
                        .courseName(detail.getCourseName())
                        .collegeId(detail.getCollegeId())
                        .majorId(detail.getMajorId())
                        .totalCredits(detail.getTotalCredits())
                        .totalHours(detail.getTotalHours())
                        .totalWeeks(detail.getTotalWeeks())
                        .hoursUnit(detail.getHoursUnit())
                        .hourTeach(detail.getHourTeach())
                        .hourPractice(detail.getHourPractice())
                        .hourOperation(detail.getHourOperation())
                        .hourOutside(detail.getHourOutside())
                        .hourWeek(detail.getHourWeek())
                        .requiredElective(detail.getRequiredElective())
                        .term(detail.getTerm())
                        .remark(detail.getRemark())
                        .version(detail.getVersion())
                        .build();
                trainingProgramDetailMapper.insert(newDetail);
            }
        }

        recordChangeLogs(rollbackVersion.getId(), currentDetails, targetDetails);

        trainingProgram.setCurrentVersion(rollbackVersion.getVersionNumber());
        trainingProgram.setLastVersionId(rollbackVersion.getId());
        trainingProgramMapper.updateById(trainingProgram);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archiveVersion(String versionId) {
        VersionHistoryDO version = versionHistoryMapper.selectById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }

        version.setVersionStatus(VersionStatusEnum.ARCHIVED.getCode());
        versionHistoryMapper.updateById(version);
    }

    @Override
    public VersionCompareRespDTO compareVersions(VersionCompareReqDTO requestParam) {
        VersionHistoryDO sourceVersion = versionHistoryMapper.selectById(requestParam.getSourceVersionId());
        VersionHistoryDO targetVersion = versionHistoryMapper.selectById(requestParam.getTargetVersionId());

        if (sourceVersion == null || targetVersion == null) {
            throw new ServiceException("版本不存在");
        }

        List<TrainingProgramDetailDO> sourceDetails = JSON.parseArray(sourceVersion.getSnapshotData(), TrainingProgramDetailDO.class);
        List<TrainingProgramDetailDO> targetDetails = JSON.parseArray(targetVersion.getSnapshotData(), TrainingProgramDetailDO.class);

        if (sourceDetails == null) {
            sourceDetails = new ArrayList<>();
        }
        if (targetDetails == null) {
            targetDetails = new ArrayList<>();
        }

        VersionCompareRespDTO response = new VersionCompareRespDTO();

        VersionCompareRespDTO.VersionInfo sourceInfo = new VersionCompareRespDTO.VersionInfo();
        sourceInfo.setId(sourceVersion.getId().toString());
        sourceInfo.setVersionNumber(sourceVersion.getVersionNumber());
        sourceInfo.setVersionName(sourceVersion.getVersionName());
        sourceInfo.setCreateTime(sourceVersion.getCreateTime().toString());
        response.setSourceVersion(sourceInfo);

        VersionCompareRespDTO.VersionInfo targetInfo = new VersionCompareRespDTO.VersionInfo();
        targetInfo.setId(targetVersion.getId().toString());
        targetInfo.setVersionNumber(targetVersion.getVersionNumber());
        targetInfo.setVersionName(targetVersion.getVersionName());
        targetInfo.setCreateTime(targetVersion.getCreateTime().toString());
        response.setTargetVersion(targetInfo);

        List<VersionCompareRespDTO.VersionDifference> differences = new ArrayList<>();
        VersionCompareRespDTO.DifferenceStatistics statistics = new VersionCompareRespDTO.DifferenceStatistics();
        statistics.setAddCount(0);
        statistics.setUpdateCount(0);
        statistics.setDeleteCount(0);

        Map<Long, TrainingProgramDetailDO> sourceMap = sourceDetails.stream()
            .collect(Collectors.toMap(TrainingProgramDetailDO::getCourseId, d -> d, (a, b) -> a));
        Map<Long, TrainingProgramDetailDO> targetMap = targetDetails.stream()
            .collect(Collectors.toMap(TrainingProgramDetailDO::getCourseId, d -> d, (a, b) -> a));

        for (TrainingProgramDetailDO targetDetail : targetMap.values()) {
            if (!sourceMap.containsKey(targetDetail.getCourseId())) {
                VersionCompareRespDTO.VersionDifference diff = new VersionCompareRespDTO.VersionDifference();
                diff.setChangeType("ADD");
                diff.setChangeTarget("detail");
                diff.setCourseName(targetDetail.getCourseName());
                diff.setNewValue(targetDetail);
                diff.setDescription("新增课程：" + targetDetail.getCourseName());
                differences.add(diff);
                statistics.setAddCount(statistics.getAddCount() + 1);
            }
        }

        for (TrainingProgramDetailDO sourceDetail : sourceMap.values()) {
            if (!targetMap.containsKey(sourceDetail.getCourseId())) {
                VersionCompareRespDTO.VersionDifference diff = new VersionCompareRespDTO.VersionDifference();
                diff.setChangeType("DELETE");
                diff.setChangeTarget("detail");
                diff.setCourseName(sourceDetail.getCourseName());
                diff.setOldValue(sourceDetail);
                diff.setDescription("删除课程：" + sourceDetail.getCourseName());
                differences.add(diff);
                statistics.setDeleteCount(statistics.getDeleteCount() + 1);
            } else {
                TrainingProgramDetailDO targetDetail = targetMap.get(sourceDetail.getCourseId());
                List<String> changedFields = compareDetailFields(sourceDetail, targetDetail);
                if (!changedFields.isEmpty()) {
                    VersionCompareRespDTO.VersionDifference diff = new VersionCompareRespDTO.VersionDifference();
                    diff.setChangeType("UPDATE");
                    diff.setChangeTarget("detail");
                    diff.setCourseName(sourceDetail.getCourseName());
                    diff.setOldValue(sourceDetail);
                    diff.setNewValue(targetDetail);
                    diff.setChangedFields(changedFields);
                    diff.setDescription("修改课程【" + sourceDetail.getCourseName() + "】：" + String.join("、", changedFields));
                    differences.add(diff);
                    statistics.setUpdateCount(statistics.getUpdateCount() + 1);
                }
            }
        }

        statistics.setTotalCount(statistics.getAddCount() + statistics.getUpdateCount() + statistics.getDeleteCount());
        response.setDifferences(differences);
        response.setStatistics(statistics);

        return response;
    }

    @Override
    public VersionListRespDTO getVersionDetail(String versionId) {
        VersionHistoryDO version = versionHistoryMapper.selectById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }
        return convertToListRespDTO(version);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVersion(String versionId) {
        VersionHistoryDO version = versionHistoryMapper.selectById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }

        if (VersionStatusEnum.PUBLISHED.getCode().equals(version.getVersionStatus())) {
            throw new ServiceException("已发布的版本不能删除");
        }

        version.setDelFlag(1);
        versionHistoryMapper.updateById(version);
    }

    @Override
    public List<TrainingProgramDetailSelectRespDTO> getVersionSnapshotDetail(String versionId) {
        VersionHistoryDO version = versionHistoryMapper.selectById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }

        String snapshotData = version.getSnapshotData();
        if (snapshotData == null || snapshotData.isEmpty()) {
            log.warn("版本 {} 的快照数据为空，尝试从当前数据库查询", versionId);
            List<TrainingProgramDetailDO> currentDetails = trainingProgramService.selectTrainingProgramDetailDOs(version.getTrainingProgramId().toString());
            if (currentDetails == null || currentDetails.isEmpty()) {
                return new ArrayList<>();
            }
            return convertToDetailSelectRespDTO(currentDetails, version.getTrainingProgramId());
        }

        List<TrainingProgramDetailDO> details = JSON.parseArray(snapshotData, TrainingProgramDetailDO.class);
        if (details == null || details.isEmpty()) {
            return new ArrayList<>();
        }

        return convertToDetailSelectRespDTO(details, version.getTrainingProgramId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveChangesAndCreateVersion(VersionSaveChangesReqDTO requestParam) {
        Long trainingProgramId = Long.parseLong(requestParam.getTrainingProgramId());

        TrainingProgramDO trainingProgram = trainingProgramMapper.selectById(trainingProgramId);
        if (trainingProgram == null) {
            throw new ServiceException("培养方案不存在");
        }

        Integer currentVersion = trainingProgram.getCurrentVersion() != null ? trainingProgram.getCurrentVersion() : 0;
        Integer newVersionNumber = currentVersion + 1;

        List<TrainingProgramDetailDO> oldDetails = trainingProgramService.selectTrainingProgramDetailDOs(trainingProgramId.toString());
        String oldSnapshot = JSON.toJSONString(oldDetails);
        log.info("保存修改前获取旧数据，版本号: {}, 课程数量: {}", currentVersion, oldDetails.size());

        if (currentVersion > 0) {
            LambdaQueryWrapper<VersionHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VersionHistoryDO::getTrainingProgramId, trainingProgramId)
                       .eq(VersionHistoryDO::getVersionNumber, currentVersion)
                       .eq(VersionHistoryDO::getDelFlag, 0)
                       .orderByDesc(VersionHistoryDO::getCreateTime)
                       .last("LIMIT 1");
            VersionHistoryDO oldVersionHistory = versionHistoryMapper.selectOne(queryWrapper);
            
            if (oldVersionHistory != null && (oldVersionHistory.getSnapshotData() == null || oldVersionHistory.getSnapshotData().isEmpty())) {
                log.info("更新旧版本 {} 的快照数据", currentVersion);
                oldVersionHistory.setSnapshotData(oldSnapshot);
                versionHistoryMapper.updateById(oldVersionHistory);
            }
        }

        if (requestParam.getDeletedCourseIds() != null && !requestParam.getDeletedCourseIds().isEmpty()) {
            for (String courseId : requestParam.getDeletedCourseIds()) {
                trainingProgramService.deleteTrainingProgramDetail(courseId);
            }
        }

        if (requestParam.getAddedCourses() != null && !requestParam.getAddedCourses().isEmpty()) {
            for (VersionSaveChangesReqDTO.CourseChangeItem item : requestParam.getAddedCourses()) {
                CourseDO course = courseMapper.selectById(Long.parseLong(item.getCourseId()));
                String courseName = item.getCourseName();
                Integer courseNature = item.getCourseNature();
                Long collegeId = item.getCollegeId() != null ? Long.parseLong(item.getCollegeId()) : null;
                
                if (course != null) {
                    if (courseName == null || courseName.isEmpty()) {
                        courseName = course.getCourseName();
                    }
                    if (courseNature == null) {
                        courseNature = course.getCourseNature();
                    }
                    if (collegeId == null) {
                        collegeId = course.getCollegeId();
                    }
                }
                
                TrainingProgramDetailDO detailDO = TrainingProgramDetailDO.builder()
                        .id(IdUtil.getSnowflakeNextId())
                        .trainingProgramId(trainingProgramId)
                        .courseId(Long.parseLong(item.getCourseId()))
                        .courseNature(courseNature != null ? courseNature : 0)
                        .courseName(courseName != null ? courseName : "")
                        .collegeId(collegeId)
                        .majorId(item.getMajorId() != null ? Long.parseLong(item.getMajorId()) : null)
                        .totalCredits(item.getTotalCredits())
                        .totalHours(item.getTotalHours())
                        .totalWeeks(item.getTotalWeeks())
                        .hoursUnit(item.getHoursUnit() != null ? item.getHoursUnit() : 0)
                        .hourTeach(item.getHourTeach())
                        .hourPractice(item.getHourPractice())
                        .hourOperation(item.getHourOperation())
                        .hourOutside(item.getHourOutside())
                        .hourWeek(item.getHourWeek())
                        .requiredElective(item.getRequiredElective())
                        .term(item.getTerm())
                        .remark(item.getRemark())
                        .version(newVersionNumber)
                        .build();
                trainingProgramDetailMapper.insert(detailDO);
            }
        }

        if (requestParam.getUpdatedCourses() != null && !requestParam.getUpdatedCourses().isEmpty()) {
            for (VersionSaveChangesReqDTO.CourseChangeItem item : requestParam.getUpdatedCourses()) {
                TrainingProgramDetailDO existingDetail = trainingProgramDetailMapper.selectById(Long.parseLong(item.getId()));
                if (existingDetail != null) {
                    existingDetail.setCollegeId(item.getCollegeId() != null ? Long.parseLong(item.getCollegeId()) : null);
                    existingDetail.setMajorId(item.getMajorId() != null ? Long.parseLong(item.getMajorId()) : null);
                    existingDetail.setTotalCredits(item.getTotalCredits());
                    existingDetail.setTotalHours(item.getTotalHours());
                    existingDetail.setTotalWeeks(item.getTotalWeeks());
                    existingDetail.setHoursUnit(item.getHoursUnit() != null ? item.getHoursUnit() : 0);
                    existingDetail.setHourTeach(item.getHourTeach());
                    existingDetail.setHourPractice(item.getHourPractice());
                    existingDetail.setHourOperation(item.getHourOperation());
                    existingDetail.setHourOutside(item.getHourOutside());
                    existingDetail.setHourWeek(item.getHourWeek());
                    existingDetail.setRequiredElective(item.getRequiredElective());
                    existingDetail.setTerm(item.getTerm());
                    existingDetail.setRemark(item.getRemark());
                    existingDetail.setVersion(newVersionNumber);
                    trainingProgramDetailMapper.updateById(existingDetail);
                }
            }
        }

        List<TrainingProgramDetailDO> newDetails = trainingProgramDetailMapper.selectList(
            new LambdaQueryWrapper<TrainingProgramDetailDO>()
                .eq(TrainingProgramDetailDO::getTrainingProgramId, trainingProgramId)
                .eq(TrainingProgramDetailDO::getDelFlag, 0)
        );
        String newSnapshot = JSON.toJSONString(newDetails);
        log.info("保存修改后获取新数据，版本号: {}, 课程数量: {}", newVersionNumber, newDetails.size());

        Long userId = SecurityUtils.getCurrentUserId();
        String userName = SecurityUtils.getCurrentUsername();

        String versionName = requestParam.getVersionName() != null && !requestParam.getVersionName().isEmpty()
                ? requestParam.getVersionName()
                : trainingProgram.getName() + " - V" + newVersionNumber;

        VersionHistoryDO versionHistory = VersionHistoryDO.builder()
                .id(IdUtil.getSnowflakeNextId())
                .trainingProgramId(trainingProgramId)
                .versionNumber(newVersionNumber)
                .versionName(versionName)
                .versionStatus(VersionStatusEnum.PUBLISHED.getCode())
                .changeDescription(requestParam.getChangeDescription() != null ? requestParam.getChangeDescription() : "手动保存创建新版本")
                .snapshotData(newSnapshot)
                .creatorId(userId)
                .creatorName(userName)
                .publishTime(LocalDateTime.now())
                .publishUserId(userId)
                .publishUserName(userName)
                .build();

        versionHistoryMapper.insert(versionHistory);

        recordChangeLogs(versionHistory.getId(), oldDetails, newDetails);

        trainingProgram.setCurrentVersion(newVersionNumber);
        trainingProgram.setLastVersionId(versionHistory.getId());
        trainingProgram.setVersionStatus(VersionStatusEnum.PUBLISHED.getCode());
        trainingProgramMapper.updateById(trainingProgram);
    }

    private List<TrainingProgramDetailSelectRespDTO> convertToDetailSelectRespDTO(List<TrainingProgramDetailDO> details, Long trainingProgramId) {
        TrainingProgramDO trainingProgram = trainingProgramMapper.selectById(trainingProgramId);
        String programName = trainingProgram != null ? trainingProgram.getName() : "";

        Map<Long, String> collegeNameMap = new HashMap<>();
        Map<Long, String> majorNameMap = new HashMap<>();
        Map<Long, String> courseTypeMap = new HashMap<>();

        for (TrainingProgramDetailDO detail : details) {
            if (detail.getCollegeId() != null && !collegeNameMap.containsKey(detail.getCollegeId())) {
                collegeNameMap.put(detail.getCollegeId(), getCollegeName(detail.getCollegeId()));
            }
            if (detail.getMajorId() != null && !majorNameMap.containsKey(detail.getMajorId())) {
                majorNameMap.put(detail.getMajorId(), getMajorName(detail.getMajorId()));
            }
            if (detail.getCourseId() != null && !courseTypeMap.containsKey(detail.getCourseId())) {
                courseTypeMap.put(detail.getCourseId(), getCourseType(detail.getCourseId()));
            }
        }

        return details.stream().map(detail -> {
            TrainingProgramDetailSelectRespDTO dto = new TrainingProgramDetailSelectRespDTO();
            dto.setId(detail.getId());
            dto.setName(programName);
            dto.setCourseName(detail.getCourseName());
            dto.setCourseNature(detail.getCourseNature());
            dto.setCourseType(courseTypeMap.getOrDefault(detail.getCourseId(), ""));
            dto.setCollegeName(collegeNameMap.getOrDefault(detail.getCollegeId(), ""));
            dto.setCollegeId(detail.getCollegeId());
            dto.setMajorName(majorNameMap.getOrDefault(detail.getMajorId(), ""));
            dto.setMajorId(detail.getMajorId());
            dto.setTotalCredits(detail.getTotalCredits());
            dto.setTotalHours(detail.getTotalHours());
            dto.setTotalWeeks(detail.getTotalWeeks());
            dto.setHoursUnit(detail.getHoursUnit());
            dto.setHourTeach(detail.getHourTeach());
            dto.setHourPractice(detail.getHourPractice());
            dto.setHourOperation(detail.getHourOperation());
            dto.setHourOutside(detail.getHourOutside());
            dto.setHourWeek(detail.getHourWeek());
            dto.setRequiredElective(detail.getRequiredElective());
            dto.setTerm(detail.getTerm());
            dto.setRemark(detail.getRemark());
            dto.setVersion(detail.getVersion());
            return dto;
        }).collect(Collectors.toList());
    }

    private String getCourseType(Long courseId) {
        if (courseId == null) return "";
        try {
            CourseDO course = courseMapper.selectById(courseId);
            if (course != null && course.getDictId() != null) {
                SysDictDO dict = sysDictMapper.selectById(course.getDictId());
                return dict != null ? dict.getDictName() : "";
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private String getCollegeName(Long collegeId) {
        if (collegeId == null) return "";
        try {
            var college = collegeMapper.selectById(collegeId);
            return college != null ? college.getCollegeName() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String getMajorName(Long majorId) {
        if (majorId == null) return "";
        try {
            var major = majorMapper.selectById(majorId);
            return major != null ? major.getMajorName() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private VersionListRespDTO convertToListRespDTO(VersionHistoryDO version) {
        VersionListRespDTO dto = new VersionListRespDTO();
        BeanUtil.copyProperties(version, dto);

        dto.setId(version.getId().toString());
        dto.setTrainingProgramId(version.getTrainingProgramId().toString());
        dto.setCreatorId(version.getCreatorId().toString());

        VersionStatusEnum statusEnum = VersionStatusEnum.getByCode(version.getVersionStatus());
        if (statusEnum != null) {
            dto.setVersionStatusDesc(statusEnum.getDesc());
        }

        TrainingProgramDO trainingProgram = trainingProgramMapper.selectById(version.getTrainingProgramId());
        if (trainingProgram != null) {
            dto.setTrainingProgramName(trainingProgram.getName());
        }

        return dto;
    }

    @Override
    public void exportVersionToExcel(String versionId, HttpServletResponse response) {
        VersionHistoryDO version = versionHistoryMapper.selectById(versionId);
        if (version == null) {
            throw new ServiceException("版本不存在");
        }

        List<TrainingProgramDetailSelectRespDTO> dataList = getVersionSnapshotDetail(versionId);
        if (dataList.isEmpty()) {
            throw new ServiceException("该版本没有课程数据");
        }

        try {
            List<TrainingProgramExcelTemplate> excelDataList = new ArrayList<>(dataList.stream()
                    .map(data -> {
                        TrainingProgramExcelTemplate excelDTO = new TrainingProgramExcelTemplate();
                        BeanUtil.copyProperties(data, excelDTO);
                        buildTotalTime(data, excelDTO);
                        excelDTO.setElectiveCreditRequirement(data.getRequiredElective() != null ? data.getRequiredElective().toString() : "");
                        excelDTO.setElectiveGroupCode("");
                        return excelDTO;
                    })
                    .toList());

            Map<String, Integer> courseTypeSortMap = new HashMap<>();
            List<SysDictDO> dictList = sysDictMapper.selectList(
                new LambdaQueryWrapper<SysDictDO>().eq(SysDictDO::getDictType, "course_type")
            );
            for (SysDictDO dict : dictList) {
                courseTypeSortMap.put(dict.getDictName(), dict.getSortOrder());
            }

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

            String fileName = URLEncoder.encode(dataList.get(0).getName() + "_V" + version.getVersionNumber() + "_专业设置及学时分配表.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            EasyExcel.write(baos, TrainingProgramExcelTemplate.class)
                    .sheet("培养计划")
                    .doWrite(finalList);

            byte[] mergedBytes = ExcelExportUtils.mergeConsecutiveSameCells(
                    baos.toByteArray(), 0, 0, 1, 12
            );

            response.getOutputStream().write(mergedBytes);
            response.getOutputStream().flush();

        } catch (IOException e) {
            throw new ServiceException("导出Excel失败: " + e.getMessage());
        }
    }

    private void buildTotalTime(TrainingProgramDetailSelectRespDTO data, TrainingProgramExcelTemplate excelDTO) {
        if (data.getHoursUnit() != null && data.getHoursUnit() == 1) {
            excelDTO.setTotalHours(data.getTotalWeeks() != null ? data.getTotalWeeks() + "周" : "");
        } else {
            excelDTO.setTotalHours(data.getTotalHours() != null ? data.getTotalHours() + "学时" : "");
        }
    }
}
