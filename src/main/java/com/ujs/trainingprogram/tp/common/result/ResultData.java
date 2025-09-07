package com.ujs.trainingprogram.tp.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询返回对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultData {
    private Long size;//当前每页显示数
    private Long total;//总条数
    private Long cur;//当前页数
    private Object data;//返回容器对象

}
