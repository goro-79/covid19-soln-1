package com.grsdev7.covid19.Covid19Soln1.dto;

import com.grsdev7.covid19.Covid19Soln1.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.With;

@Data
@Builder
@With
public class UserDto {
    private String email;
    private String name;
    private String city;

    public static UserDto from(User user) {
        return UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .city(user.getCity())
                .build();
    }


    public static User toUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .city(userDto.getCity())
                .build();
    }
}
