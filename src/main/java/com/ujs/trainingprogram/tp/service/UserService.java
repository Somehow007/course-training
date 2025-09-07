package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.model.User;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {
    ResultData selectWithWrapper(long cur, long size, QueryWrapper<User> wrapper);
    public void exportUserToExcel(HttpServletResponse response);
}
