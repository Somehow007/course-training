package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.TrainingProgramDetailDO;
import com.ujs.trainingprogram.tp.dao.entity.VersionChangeLogDO;
import com.ujs.trainingprogram.tp.dao.entity.VersionHistoryDO;
import com.ujs.trainingprogram.tp.dto.req.version.VersionCompareReqDTO;
import com.ujs.trainingprogram.tp.dto.req.version.VersionCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.version.VersionPageReqDTO;
import com.ujs.trainingprogram.tp.dto.req.version.VersionSaveChangesReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.trainingprogram.TrainingProgramDetailSelectRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.version.VersionCompareRespDTO;
import com.ujs.trainingprogram.tp.dto.resp.version.VersionListRespDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface VersionHistoryService extends IService<VersionHistoryDO> {

    IPage<VersionListRespDTO> pageVersionHistory(VersionPageReqDTO requestParam);

    void createVersion(VersionCreateReqDTO requestParam);

    void publishVersion(String versionId);

    void rollbackVersion(String versionId);

    void archiveVersion(String versionId);

    VersionCompareRespDTO compareVersions(VersionCompareReqDTO requestParam);

    VersionListRespDTO getVersionDetail(String versionId);

    void deleteVersion(String versionId);

    VersionHistoryDO createVersionFromImport(Long trainingProgramId, String changeDescription, boolean isFirstVersion);

    List<VersionListRespDTO> listVersionsByTrainingProgramId(String trainingProgramId);

    List<VersionChangeLogDO> getVersionChangeLogs(String versionId);

    void recordChangeLogs(Long versionId, List<TrainingProgramDetailDO> oldDetails, List<TrainingProgramDetailDO> newDetails);

    List<TrainingProgramDetailSelectRespDTO> getVersionSnapshotDetail(String versionId);

    void saveChangesAndCreateVersion(VersionSaveChangesReqDTO requestParam);

    void exportVersionToExcel(String versionId, HttpServletResponse response);
}
