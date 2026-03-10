package com.ujs.trainingprogram.tp.dto.resp.trainingprogram;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ujs.trainingprogram.tp.common.enums.CourseTypeEnum;
import lombok.Data;

@Data
public class TrainingProgramSelectRespDTO {

    /**
     * id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 培养计划名称
     */
    private String name;
}
