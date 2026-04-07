package com.ujs.trainingprogram.tp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ujs.trainingprogram.tp.dao.entity.SysDictDO;
import com.ujs.trainingprogram.tp.dto.req.sysdict.SysDictCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.sysdict.SysDictPageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.req.sysdict.SysDictUpdateReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.sysdict.SysDictPageQueryRespDTO;

import java.util.List;

/**
 * 系统字典业务逻辑层
 */
public interface SysDictService extends IService<SysDictDO> {

    void createSysDict(SysDictCreateReqDTO requestParam);

    void updateSysDict(SysDictUpdateReqDTO requestParam);

    void deleteSysDict(Long id);

    IPage<SysDictPageQueryRespDTO> pageQuerySysDict(SysDictPageQueryReqDTO requestParam);

    List<SysDictDO> listSysDict();

    List<SysDictDO> listCourseTypeSysDict();

    List<String> listDictTypes();
}
