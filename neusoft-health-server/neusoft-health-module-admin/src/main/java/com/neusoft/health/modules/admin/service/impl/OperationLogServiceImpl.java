package com.neusoft.health.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.modules.admin.entity.OperationLog;
import com.neusoft.health.modules.admin.mapper.OperationLogMapper;
import com.neusoft.health.modules.admin.service.OperationLogService;
import com.neusoft.health.modules.admin.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public List<OperationLogVO> listLogs(Long userId, String action, int page, int size) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .orderByDesc(OperationLog::getCreatedTime);
        if (userId != null) {
            wrapper.eq(OperationLog::getUserId, userId);
        }
        if (action != null && !action.isEmpty()) {
            wrapper.eq(OperationLog::getOperation, action);
        }
        List<OperationLog> logs = list(wrapper);
        return logs.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void recordLog(Long userId, String username, String action, String detail) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(action);
        log.setRequestParams(detail);
        save(log);
    }

    private OperationLogVO toVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }
}
