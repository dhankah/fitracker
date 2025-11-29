package com.fitracker.mapper;

import com.fitracker.dto.RegisterRequest;
import com.fitracker.dto.UpdateProfileRequest;
import com.fitracker.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "user.weightKg", source = "userRequest.weightKg")
    @Mapping(target = "user.heightCm", source = "userRequest.heightCm")
    @Mapping(target = "user.age", source = "userRequest.age")
    @Mapping(target = "user.sex", source = "userRequest.sex")
    @Mapping(target = "user.goal", source = "userRequest.goal")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void populateUser(@MappingTarget User user, UpdateProfileRequest userRequest);

    @Mapping(target = "weightKg", source = "request.weightKg")
    @Mapping(target = "heightCm", source = "request.heightCm")
    @Mapping(target = "age", source = "request.age")
    @Mapping(target = "sex", source = "request.sex")
    @Mapping(target = "goal", source = "request.goal")
    @Mapping(target = "email", source = "request.email")
    User mapToUser(RegisterRequest request);
}
