package dev.resumate.service;

import dev.resumate.converter.MemberConverter;
import dev.resumate.domain.Member;
import dev.resumate.dto.MemberRequestDTO;
import dev.resumate.global.exception.BusinessBaseException;
import dev.resumate.global.exception.ErrorCode;
import dev.resumate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void join(MemberRequestDTO.MemberJoinDto request) {

        if (!memberRepository.existsMemberByEmail(request.getEmail())) {
            throw new BusinessBaseException(ErrorCode.MEMBER_ALREADY_EXIST);
        }

        Member newMember = MemberConverter.toMember(request);
        memberRepository.save(newMember);
    }
}
