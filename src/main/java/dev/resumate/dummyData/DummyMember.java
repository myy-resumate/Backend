package dev.resumate.dummyData;

import dev.resumate.domain.Member;
import dev.resumate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class DummyMember {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    /**
     * 500명의 회원 생성
     */
    public void generate() {
        Faker faker = new Faker(new Locale("ko", "ko"));

        for (int i = 500; i < 5000; i++) {
            String name = faker.name().name();
            Member member = Member.builder()
                    .name(name)
                    .email(name + i + "@naver.com")  //고유해야해서 i 추가
                    .password(encoder.encode("1234"))
                    .build();

            memberRepository.save(member);
        }
    }
}
