package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
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
import com.ujs.trainingprogram.tp.dto.resp.version.*;
import com.ujs.trainingprogram.tp.service.*;
import com.ujs.trainingprogram.tp.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
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

    private static final Set<String> EXCLUDED_COMPARE_FIELDS = Set.of(
            "id", "trainingProgramId", "serialVersionUID", "createTime", "updateTime", "delFlag"
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
            Map.entry("remark", "备注"),
            Map.entry("version", "版本号")
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
}
