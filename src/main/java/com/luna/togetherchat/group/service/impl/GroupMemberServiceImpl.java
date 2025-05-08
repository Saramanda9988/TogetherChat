package com.luna.togetherchat.group.service.impl;

import com.luna.togetherchat.group.dao.GroupDao;
import com.luna.togetherchat.group.domain.entity.GroupMember;
import com.luna.togetherchat.group.mapper.GroupMemberMapper;
import com.luna.togetherchat.group.service.GroupMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Service
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {
    private final GroupDao groupDao;


}
