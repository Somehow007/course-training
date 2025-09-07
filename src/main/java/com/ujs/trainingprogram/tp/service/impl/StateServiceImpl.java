package com.ujs.trainingprogram.tp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ujs.trainingprogram.tp.mapper.StateMapper;
import com.ujs.trainingprogram.tp.model.State;
import com.ujs.trainingprogram.tp.service.StateService;
import org.springframework.stereotype.Service;

@Service
public class StateServiceImpl extends ServiceImpl<StateMapper, State> implements StateService {
}
