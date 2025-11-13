package com.ujs.trainingprogram.tp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.common.exception.ClientException;
import com.ujs.trainingprogram.tp.dao.entity.SysDictDO;
import com.ujs.trainingprogram.tp.dao.mapper.SysDictMapper;
import com.ujs.trainingprogram.tp.dto.req.sysdict.SysDictCreateReqDTO;
import com.ujs.trainingprogram.tp.dto.req.sysdict.SysDictPageQueryReqDTO;
import com.ujs.trainingprogram.tp.dto.resp.sysdict.SysDictPageQueryRespDTO;
import com.ujs.trainingprogram.tp.service.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 系统字典业务逻辑实现层
 */
@Slf4j
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDictDO> implements SysDictService {
    private final SysDictMapper sysDictMapper;

    public SysDictServiceImpl(SysDictMapper sysDictMapper) {
        this.sysDictMapper = sysDictMapper;
    }

    @Override
    public void createSysDict(SysDictCreateReqDTO requestParam) {
        SysDictDO sysDictDO = BeanUtil.toBean(requestParam, SysDictDO.class);
        sysDictDO.setId(IdUtil.getSnowflakeNextId());
        try {
            sysDictMapper.insert(sysDictDO);
        } catch (DuplicateKeyException ex) {
            throw new ClientException("创建系统字典失败：重复创建");
        }
    }

    @Override
    public void deleteSysDict(Long id) {
        LambdaUpdateWrapper<SysDictDO> queryWrapper = Wrappers.lambdaUpdate(SysDictDO.class)
                .eq(SysDictDO::getId, id)
                .eq(SysDictDO::getDelFlag, 0)
                .set(SysDictDO::getDelFlag, 1);
        baseMapper.update(null, queryWrapper);
    }

    @Override
    public IPage<SysDictPageQueryRespDTO> pageQuerySysDict(SysDictPageQueryReqDTO requestParam) {
        LambdaQueryWrapper<SysDictDO> queryWrapper = Wrappers.lambdaQuery(SysDictDO.class)
                .eq(StrUtil.isNotBlank(requestParam.getDictCode()), SysDictDO::getDictCode, requestParam.getDictCode())
                .eq(StrUtil.isNotBlank(requestParam.getDictName()), SysDictDO::getDictName, requestParam.getDictName())
                .eq(StrUtil.isNotBlank(requestParam.getDictType()), SysDictDO::getDictType, requestParam.getDictType())
                .eq(SysDictDO::getDelFlag, 0);
        IPage<SysDictDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each -> BeanUtil.toBean(each, SysDictPageQueryRespDTO.class));
    }
}
