package com.neusoft.health.modules.admin.service.impl;

import com.neusoft.health.modules.admin.service.ContentReviewService;
import com.neusoft.health.modules.consultation.dto.MessageReviewDTO;
import com.neusoft.health.modules.consultation.service.MessageService;
import com.neusoft.health.modules.consultation.vo.MessageDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentReviewServiceImpl implements ContentReviewService {

    private final MessageService messageService;

    @Override
    public List<MessageDetailVO> listPendingReview(int page, int size) {
        return messageService.listPendingReview(page, size);
    }

    @Override
    public void reviewMessage(MessageReviewDTO dto, Long reviewerId) {
        messageService.reviewMessage(dto, reviewerId);
    }
}
