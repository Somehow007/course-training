package com.ujs.trainingprogram.tp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.dao.mapper.StateMapper;
import com.ujs.trainingprogram.tp.dao.entity.StateDO;
import com.ujs.trainingprogram.tp.service.StateService;
import org.springframework.stereotype.Service;

@Service
public class StateServiceImpl extends ServiceImpl<StateMapper, StateDO> implements StateService {
}
