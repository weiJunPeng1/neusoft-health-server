package com.neusoft.health.modules.consultation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.common.enums.UserStatusEnum;
import com.neusoft.health.modules.consultation.entity.SensitiveWord;
import com.neusoft.health.modules.consultation.mapper.SensitiveWordMapper;
import com.neusoft.health.modules.consultation.service.SensitiveWordService;
import com.neusoft.health.modules.consultation.vo.SensitiveWordCheckVO;
import com.neusoft.health.modules.consultation.vo.SensitiveWordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {

    @Override
    public List<SensitiveWordVO> listWords() {
        List<SensitiveWord> words = listActiveWords();
        return words.stream().map(w -> {
            SensitiveWordVO vo = new SensitiveWordVO();
            org.springframework.beans.BeanUtils.copyProperties(w, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public SensitiveWordCheckVO check(String content) {
        SensitiveWordCheckVO vo = new SensitiveWordCheckVO();
        List<String> hitWords = new ArrayList<>();
        List<SensitiveWord> words = listActiveWords();
        for (SensitiveWord word : words) {
            if (content.contains(word.getWord())) {
                hitWords.add(word.getWord());
            }
        }
        vo.setHit(!hitWords.isEmpty());
        vo.setWords(hitWords);
        return vo;
    }

    @Override
    public List<SensitiveWord> listActiveWords() {
        return list(new LambdaQueryWrapper<SensitiveWord>()
                .eq(SensitiveWord::getStatus, UserStatusEnum.NORMAL.getCode()));
    }

    @Override
    public String filterSensitiveWords(String content) {
        List<SensitiveWord> words = listActiveWords();
        String result = content;
        for (SensitiveWord word : words) {
            result = result.replaceAll(word.getWord(), "**");
        }
        return result;
    }

    @Override
    public boolean containsSensitiveWord(String content) {
        List<SensitiveWord> words = listActiveWords();
        for (SensitiveWord word : words) {
            if (content.contains(word.getWord())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getEmergencyKeyword(String content) {
        List<SensitiveWord> emergencyWords = list(new LambdaQueryWrapper<SensitiveWord>()
                .eq(SensitiveWord::getStatus, UserStatusEnum.NORMAL.getCode())
                .eq(SensitiveWord::getCategory, "医疗风险"));
        for (SensitiveWord word : emergencyWords) {
            if (content.contains(word.getWord())) {
                return word.getWord();
            }
        }
        return null;
    }

    @Override
    public boolean isEmergencyContent(String content) {
        return getEmergencyKeyword(content) != null;
    }
}