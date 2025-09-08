package com.ujs.trainingprogram.tp.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 状态实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("state")
public class StateDO {

    /**
     * 状态Id
     */
    @TableId("state_id")
    private String stateId;

    /**
     * 状态名
     */
    private String stateName;
}
