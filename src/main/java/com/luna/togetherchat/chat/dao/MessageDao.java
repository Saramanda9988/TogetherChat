package com.luna.togetherchat.chat.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.chat.domain.entity.Message;
import com.luna.togetherchat.chat.enums.MessageStatusEnum;
import com.luna.togetherchat.chat.mapper.MessageMapper;
import com.luna.togetherchat.common.domain.vo.request.CursorPageBaseRequest;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.CursorUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

}
