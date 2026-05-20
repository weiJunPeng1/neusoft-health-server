package com.neusoft.health.modules.admin.service.impl;

import com.neusoft.health.modules.admin.service.SensitiveWordManageService;
import com.neusoft.health.modules.consultation.entity.SensitiveWord;
import com.neusoft.health.modules.consultation.mapper.SensitiveWordMapper;
import com.neusoft.health.modules.consultation.vo.SensitiveWordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensitiveWordManageServiceImpl implements SensitiveWordManageService {

    private final SensitiveWordMapper sensitiveWordMapper;

    @Override
    public List<SensitiveWordVO> listAll() {
        return sensitiveWordMapper.selectList(null).stream().map(w -> {
            SensitiveWordVO vo = new SensitiveWordVO();
            BeanUtils.copyProperties(w, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public SensitiveWord create(SensitiveWord word) {
        sensitiveWordMapper.insert(word);
        return word;
    }

    @Override
    public SensitiveWord updateWord(SensitiveWord word) {
        sensitiveWordMapper.updateById(word);
        return word;
    }

    @Override
    public void deleteWord(Long id) {
        sensitiveWordMapper.deleteById(id);
    }
}
