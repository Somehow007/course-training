package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.common.result.ResultData;
import com.ujs.trainingprogram.tp.dao.entity.UserDO;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService extends IService<UserDO> {
    ResultData selectWithWrapper(long cur, long size, QueryWrapper<UserDO> wrapper);
    public void exportUserToExcel(HttpServletResponse response);
}
