package com.luna.togetherchat.group.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luna.togetherchat.common.domain.vo.request.CursorPageBaseRequest;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;
import com.luna.togetherchat.common.utils.CursorUtils;
import com.luna.togetherchat.group.domain.entity.Group;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.domain.response.GroupResponse;
import com.luna.togetherchat.group.mapper.GroupMapper;
import com.luna.togetherchat.group.mapper.GroupMemberMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GroupDao extends ServiceImpl<GroupMapper, Group>{

}
