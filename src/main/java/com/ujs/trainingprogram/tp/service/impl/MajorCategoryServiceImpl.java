//package com.ujs.trainingprogram.tp.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
//import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.ujs.trainingprogram.tp.common.constant.RedisKeyConstant;
//import com.ujs.trainingprogram.tp.dao.entity.MajorCategoryDO;
//import com.ujs.trainingprogram.tp.dao.entity.MajorDO;
//import com.ujs.trainingprogram.tp.dao.mapper.MajorCategoryMapper;
//import com.ujs.trainingprogram.tp.dto.req.MajorCategoryQueryReqDTO;
//import com.ujs.trainingprogram.tp.dto.resp.MajorCategoryQueryRespDTO;
//import com.ujs.trainingprogram.tp.service.MajorCategoryService;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RMapCache;
//import org.redisson.api.RedissonClient;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//
///**
// * 专业分类配置实现层
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class MajorCategoryServiceImpl extends ServiceImpl<MajorCategoryMapper, MajorCategoryDO> implements MajorCategoryService {
//
//    private final MajorCategoryMapper majorCategoryMapper;
//
//    // 一级缓存
//    private final Map<Long, MajorCategoryDO> cache = new ConcurrentHashMap<>();
//    private final RedissonClient redissonClient;
//
//    @Override
//    public List<MajorCategoryQueryRespDTO> listMajorCategories() {
//        LambdaQueryWrapper<MajorCategoryDO> queryWrapper = Wrappers.lambdaQuery(MajorCategoryDO.class)
//                .eq(MajorCategoryDO::getDelFlag, 0);
//        List<MajorCategoryDO> majorCategoryDOS = baseMapper.selectList(queryWrapper);
//
//        List<MajorCategoryQueryRespDTO> results = new ArrayList<>();
//        majorCategoryDOS.forEach(each -> {
//            MajorCategoryQueryRespDTO respDTO = BeanUtil.toBean(each, MajorCategoryQueryRespDTO.class);
//            results.add(respDTO);
//        });
//
//        return results;
//    }
//
//    @Override
//    public void updateMajorCategories(MajorCategoryQueryReqDTO requestParam) {
//        LambdaUpdateWrapper<MajorCategoryDO> updateWrapper = Wrappers.lambdaUpdate(MajorCategoryDO.class)
//                .eq(MajorCategoryDO::getDisciplineCategory, requestParam.getDisciplineCategory())
//                .eq(MajorCategoryDO::getProfessionalCategory, requestParam.getProfessionalCategory())
//                .eq(MajorCategoryDO::getDelFlag, 0);
//
//    }
//
//
//    @PostConstruct
//    public void initCache() {
//        refreshCache();
//    }
//
//    // ✅ 刷新缓存（分类变更时调用）
//    public synchronized void refreshCache() {
//        cache.clear();
//
//        QueryWrapper<MajorCategoryDO> wrapper = new QueryWrapper<>();
//        LambdaQueryWrapper<MajorCategoryDO> queryWrapper = Wrappers.lambdaQuery(MajorCategoryDO.class)
//                .eq(MajorCategoryDO::getDelFlag, 0);
//
//        List<MajorCategoryDO> list = majorCategoryMapper.selectList(queryWrapper);
//
//        // 二级缓存
//        RMapCache<Long, MajorCategoryDO> cacheMap = redissonClient.getMapCache(RedisKeyConstant.MAJOR_CATEGORY_KEY);
//        for (MajorCategoryDO config : list) {
//            cache.put(config.getId(), config);
//        }
//        cacheMap.putAll(cache, 30, TimeUnit.MINUTES);
//    }
//
//    // ✅ 根据 ID 获取分类配置（用于专业保存/展示）
//    public MajorCategoryDO getConfigById(Long categoryId) {
//        return cache.get(categoryId);
//    }
//
//    // ✅ 获取所有启用的分类（用于下拉框、前端展示）
//    public List<MajorCategoryDO> getAllEnabledCategories() {
//        return new ArrayList<>(cache.values());
//    }
//
//    // ✅ 检查分类是否存在且启用（用于业务校验）
//    public void validateCategory(Long categoryId) {
//        if (categoryId == null) {
//            throw new IllegalArgumentException("专业分类不能为空");
//        }
//        MajorCategoryDO config = getConfigById(categoryId);
//        if (config == null) {
//            throw new IllegalArgumentException("无效的专业分类ID: " + categoryId);
//        }
//    }
//}
