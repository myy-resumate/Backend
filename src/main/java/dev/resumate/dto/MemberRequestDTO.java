package dev.resumate.dto;

import lombok.Getter;

public class MemberRequestDTO {

    @Getter
    public static class MemberJoinDto {

        private String name;
        private String email;
        private String password;
    }

}
