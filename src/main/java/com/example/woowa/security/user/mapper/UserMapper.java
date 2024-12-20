package com.example.woowa.security.user.mapper;

import com.example.woowa.common.base.BaseLoginEntity;
import com.example.woowa.security.role.entity.Role;
import com.example.woowa.security.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "loginEntity.loginId", target = "loginId")
    @Mapping(source = "loginEntity.password", target = "password")
    @Mapping(source = "loginEntity.name", target = "name")
    @Mapping(source = "loginEntity.phoneNumber", target = "phoneNumber")
    User toUser(BaseLoginEntity loginEntity, Role role);
}
