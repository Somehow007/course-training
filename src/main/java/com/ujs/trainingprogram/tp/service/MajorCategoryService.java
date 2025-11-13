//package com.ujs.trainingprogram.tp.service;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.ujs.trainingprogram.tp.dao.entity.MajorCategoryDO;
//import com.ujs.trainingprogram.tp.dto.req.MajorCategoryQueryReqDTO;
//import com.ujs.trainingprogram.tp.dto.resp.MajorCategoryQueryRespDTO;
//
//import java.util.List;
//
///**
// * 专业分类接口层
// * todo：
// *  1. 项目启动时写入缓存
// *  2. 刷新缓存
// *  3. 获取所有启用的分类（下拉框展示）
// *  4. 检查分类是否存在且启用（业务校验）
// */
//public interface MajorCategoryService extends IService<MajorCategoryDO> {
//
//    /**
//     * 查询专业分类配置
//     *
//     * @return  查询结果返回列表
//     */
//    List<MajorCategoryQueryRespDTO> listMajorCategories();
//
//    /**
//     * 更新专业分类配置
//     *
//     * @param requestParam 更新专业分类配置请求实体
//     */
//    void updateMajorCategories(MajorCategoryQueryReqDTO requestParam);
//
//}
