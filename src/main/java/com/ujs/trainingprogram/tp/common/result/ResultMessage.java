package com.ujs.trainingprogram.tp.common.result;

import com.ujs.trainingprogram.tp.common.Constant;
import lombok.Data;

/**
 * 返回消息对象
 */
@Data
public class ResultMessage {
    private Integer code;
    private String msg;

    public ResultMessage() {

    }

    public ResultMessage(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public static final ResultMessage LOGIN_SUCCESS = new ResultMessage(Constant.OK, "登录成功");
    public static final ResultMessage LOGIN_ERROR_PASS = new ResultMessage(Constant.ERROR, "登录失败，用户名或密码不正确");
    public static final ResultMessage LOGIN_ERROR_CODE = new ResultMessage(Constant.ERROR, "登录失败，验证码不正确");

    public static final ResultMessage UPDATE_ERROR = new ResultMessage(Constant.ERROR, "更新失败");
    public static final ResultMessage UPDATE_SUCCESS = new ResultMessage(Constant.ERROR, "更新成功");

    public static final ResultMessage PARAM_MISS = new ResultMessage(Constant.ERROR, "参数丢失");

    public static final ResultMessage ADD_SUCCESS = new ResultMessage(Constant.ERROR, "添加成功");
    public static final ResultMessage ADD_ERROR = new ResultMessage(Constant.ERROR, "添加失败");

    public static final ResultMessage DELETE_SUCCESS = new ResultMessage(Constant.ERROR, "删除成功");
    public static final ResultMessage DELETE_ERROR = new ResultMessage(Constant.ERROR, "删除成功");
    public static final ResultMessage UNAUTHORIZED_ERROR = new ResultMessage(Constant.ERROR, "认证失败");


}
