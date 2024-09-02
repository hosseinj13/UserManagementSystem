package com.example.usermanagementsystem.mapper;


import com.example.usermanagementsystem.dto.LoginRequest;
import com.example.usermanagementsystem.dto.SignupRequest;
import com.example.usermanagementsystem.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(SignupRequest signupRequest);

    SignupRequest toSignupRequest(User user);

    User toEntity(LoginRequest loginRequest);

    LoginRequest toLoginRequest(User user);
}
