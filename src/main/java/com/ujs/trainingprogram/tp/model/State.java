package com.ujs.trainingprogram.tp.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 状态POJO
 */
@Data
@TableName("state")
public class State {
    @TableId("state_id")
    private String stateId;//状态Id
    private String stateName;//状态名
}
