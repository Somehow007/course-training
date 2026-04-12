package com.ujs.trainingprogram.tp.dto.resp.version;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "版本对比响应DTO")
public class VersionCompareRespDTO {

    @Schema(description = "源版本信息")
    private VersionInfo sourceVersion;

    @Schema(description = "目标版本信息")
    private VersionInfo targetVersion;

    @Schema(description = "差异列表")
    private List<VersionDifference> differences;

    @Schema(description = "差异统计")
    private DifferenceStatistics statistics;

    @Data
    @Schema(description = "版本信息")
    public static class VersionInfo {
        @Schema(description = "版本ID")
        private String id;

        @Schema(description = "版本号")
        private Integer versionNumber;

        @Schema(description = "版本名称")
        private String versionName;

        @Schema(description = "创建时间")
        private String createTime;
    }

    @Data
    @Schema(description = "版本差异")
    public static class VersionDifference {
        @Schema(description = "差异类型（ADD/UPDATE/DELETE）")
        private String changeType;

        @Schema(description = "变更对象（course/detail）")
        private String changeTarget;

        @Schema(description = "课程名称")
        private String courseName;

        @Schema(description = "旧值")
        private Object oldValue;

        @Schema(description = "新值")
        private Object newValue;

        @Schema(description = "差异描述")
        private String description;

        @Schema(description = "变更字段列表")
        private List<String> changedFields;
    }

    @Data
    @Schema(description = "差异统计")
    public static class DifferenceStatistics {
        @Schema(description = "新增数量")
        private Integer addCount;

        @Schema(description = "修改数量")
        private Integer updateCount;

        @Schema(description = "删除数量")
        private Integer deleteCount;

        @Schema(description = "总差异数")
        private Integer totalCount;
    }
}
