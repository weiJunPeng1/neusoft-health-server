package com.neusoft.health.modules.system.service.impl;

import com.neusoft.health.modules.consultation.entity.Faq;
import com.neusoft.health.modules.consultation.entity.FaqCategory;
import com.neusoft.health.modules.consultation.mapper.FaqCategoryMapper;
import com.neusoft.health.modules.consultation.mapper.FaqMapper;
import com.neusoft.health.modules.consultation.vo.FaqCategoryTreeVO;
import com.neusoft.health.modules.consultation.vo.FaqVO;
import com.neusoft.health.modules.system.entity.HealthArticle;
import com.neusoft.health.modules.system.mapper.HealthArticleMapper;
import com.neusoft.health.modules.system.service.HomeService;
import com.neusoft.health.modules.system.vo.HealthArticleVO;
import com.neusoft.health.modules.system.vo.HomeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页服务实现
 * <p>
 * 提供首页数据聚合功能
 * </p>
 *
 * @author Neusoft Health Consulting
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final FaqCategoryMapper faqCategoryMapper;
    private final FaqMapper faqMapper;
    private final HealthArticleMapper healthArticleMapper;

    @Override
    public HomeVO getHomeData() {
        List<HealthArticle> articles = healthArticleMapper.findActiveArticles(3);
        List<HealthArticleVO> articleVOs = articles.stream().map(a -> {
            HealthArticleVO vo = new HealthArticleVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setSummary(a.getSummary());
            vo.setCoverUrl(a.getCoverUrl());
            vo.setContentUrl(a.getContentUrl());
            vo.setSortOrder(a.getSortOrder());
            vo.setCreatedTime(a.getCreatedTime());
            return vo;
        }).collect(Collectors.toList());

        List<FaqCategory> categories = faqCategoryMapper.findActiveCategories();
        List<FaqCategoryTreeVO> categoryVOs = categories.stream().map(c -> {
            FaqCategoryTreeVO catVO = new FaqCategoryTreeVO();
            catVO.setId(c.getId());
            catVO.setName(c.getName());
            catVO.setIcon(c.getIcon());
            catVO.setSortOrder(c.getSortOrder());

            List<Faq> faqs = faqMapper.findActiveByCategory(c.getId());
            List<FaqVO> faqVOs = faqs.stream().map(f -> {
                FaqVO fvo = new FaqVO();
                fvo.setId(f.getId());
                fvo.setCategoryId(f.getCategoryId());
                fvo.setQuestion(f.getQuestion());
                fvo.setPresetAnswer(f.getPresetAnswer());
                fvo.setSortOrder(f.getSortOrder());
                return fvo;
            }).collect(Collectors.toList());
            catVO.setFaqList(faqVOs);
            return catVO;
        }).collect(Collectors.toList());

        HomeVO homeVO = new HomeVO();
        homeVO.setArticles(articleVOs);
        homeVO.setFaqCategories(categoryVOs);
        homeVO.setDisclaimer("【健康咨询仅供参考，不能替代专业医生诊断和治疗】");
        return homeVO;
    }
}
