package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.SysDictDO;
import com.ujs.trainingprogram.tp.dto.req.sysdict.SysDictCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.sysdict.SysDictPageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.sysdict.SysDictPageQueryRespDTO;

/**
 * 系统字典业务逻辑层
 */
public interface SysDictService extends IService<SysDictDO> {

    /**
     * 创建系统字典
     *
     * @param requestParam 请求参数
     */
    void createSysDict(SysDictCreateReqDTO requestParam);

    /**
     * 删除系统字典
     *
     * @param id 系统字典id
     */
    void deleteSysDict(Long id);

    /**
     * 分页查询系统字典
     *
     * @param requestParam 请求参数
     */
    IPage<SysDictPageQueryRespDTO> pageQuerySysDict(SysDictPageQueryReqDTO requestParam);

}
