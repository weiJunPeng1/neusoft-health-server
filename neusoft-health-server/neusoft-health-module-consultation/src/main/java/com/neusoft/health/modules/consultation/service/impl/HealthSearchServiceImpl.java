package com.neusoft.health.modules.consultation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neusoft.health.framework.ai.DeepSeekClient;
import com.neusoft.health.modules.consultation.entity.HealthSearchCache;
import com.neusoft.health.modules.consultation.mapper.HealthSearchCacheMapper;
import com.neusoft.health.modules.consultation.service.HealthSearchService;
import com.neusoft.health.modules.consultation.vo.HealthSearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthSearchServiceImpl extends ServiceImpl<HealthSearchCacheMapper, HealthSearchCache> implements HealthSearchService {

    private final DeepSeekClient deepSeekClient;

    private static final Integer SOURCE_AI = 1;

    @Override
    public HealthSearchVO search(String keyword) {
        // 空值检查
        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("Health search called with empty keyword");
            HealthSearchVO vo = new HealthSearchVO();
            vo.setDisclaimer("请输入有效的搜索关键词");
            return vo;
        }

        String normalized = normalizeKeyword(keyword);
        
        // 关键词长度限制
        if (normalized.length() > 100) {
            log.warn("Health search keyword too long: length={}", normalized.length());
            HealthSearchVO vo = new HealthSearchVO();
            vo.setDisclaimer("搜索关键词过长，请简化后重试");
            return vo;
        }

        String keywordHash = md5(normalized);

        // 优先查找精确匹配的缓存
        HealthSearchCache cached = getOne(new LambdaQueryWrapper<HealthSearchCache>()
                .eq(HealthSearchCache::getKeywordHash, keywordHash));
        if (cached != null) {
            log.debug("Health search cache hit: keyword={}", keyword);
            cached.setHitCount(cached.getHitCount() + 1);
            updateById(cached);
            return toVO(cached, true);
        }

        // 查找相似关键词
        List<HealthSearchCache> similarList = list(new LambdaQueryWrapper<HealthSearchCache>()
                .like(HealthSearchCache::getKeyword, keyword));
        if (!similarList.isEmpty()) {
            log.debug("Health search found similar: keyword={}, similarCount={}", keyword, similarList.size());
            HealthSearchCache best = similarList.get(0);
            best.setHitCount(best.getHitCount() + 1);
            updateById(best);
            return toVO(best, true);
        }

        log.info("Health search cache miss, calling AI: keyword={}", keyword);
        
        String systemPrompt = "你是一个专业的健康知识库助手，由东软健康提供。" +
                "用户正在搜索健康相关问题，请基于可靠的医学常识，为用户提供专业、准确、简洁的解答。" +
                "回答请控制在300字以内，条理清晰，分点说明。注意：你的回答仅供参考，不能替代专业医疗诊断。";

        String aiReply = deepSeekClient.chat(systemPrompt, keyword);
        
        // AI调用失败处理
        if (aiReply == null || aiReply.isEmpty()) {
            log.error("AI health search returned empty: keyword={}", keyword);
            HealthSearchVO vo = new HealthSearchVO();
            vo.setKeyword(keyword);
            vo.setDisclaimer("AI服务暂时不可用，请稍后再试");
            return vo;
        }

        log.info("AI健康搜索完成, keyword={}, replyLength={}", keyword, aiReply.length());

        HealthSearchCache newCache = new HealthSearchCache();
        newCache.setKeyword(keyword);
        newCache.setKeywordHash(keywordHash);
        newCache.setContent(aiReply);
        newCache.setSource(SOURCE_AI);
        newCache.setHitCount(1);
        save(newCache);

        HealthSearchVO vo = toVO(newCache, false);
        vo.setDisclaimer("以上内容由AI生成，仅供参考，不构成医疗诊断建议。如有身体不适，请及时就医。");
        return vo;
    }

    private String normalizeKeyword(String keyword) {
        return keyword.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    private HealthSearchVO toVO(HealthSearchCache entity, boolean isCached) {
        HealthSearchVO vo = new HealthSearchVO();
        vo.setId(entity.getId());
        vo.setKeyword(entity.getKeyword());
        vo.setContent(entity.getContent());
        vo.setSource(entity.getSource());
        if (isCached) {
            vo.setDisclaimer("以上内容来源于健康知识库，仅供参考。如有身体不适，请及时就医。");
        }
        return vo;
    }
}
