package com.ujs.trainingprogram.tp.dto.req.college;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页查找学院请求参数实体
 */
@Data
@Schema(description = "分页查找学院请求参数实体")
public class CollegePageReqDTO extends Page {

    /**
     * 学院ID
     * todo 可扩展为通过多个学院Id查询信息，List<String>
     */
    @Schema(description = "学院Id", example = "[\"1988456399996981248\", \"1988456399996981249\"]")
    private List<String> collegeIds;

    /**
     * 学院名称
     */
    @Schema(description = "学院名称", example = "[\"计算机学院\", \"数科学院\"]")
    private List<String> collegeNames;
}
