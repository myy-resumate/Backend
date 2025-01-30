package dev.resumate.converter;

import dev.resumate.domain.Member;
import dev.resumate.dto.MemberRequestDTO;

public class MemberConverter {

    public static Member toMember(MemberRequestDTO.JoinDto request) {
        return Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
}
