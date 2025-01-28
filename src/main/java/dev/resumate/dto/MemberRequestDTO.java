package dev.resumate.dto;

import lombok.Getter;

public class MemberRequestDTO {

    @Getter
    public static class JoinDto {

        private String name;
        private String email;
        private String password;
    }

    @Getter
    public static class LoginDto {

        private String email;
        private String password;
    }


}
