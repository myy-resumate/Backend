package dev.resumate.dummyData;

import dev.resumate.apiPayload.exception.BusinessBaseException;
import dev.resumate.apiPayload.exception.ErrorCode;
import dev.resumate.domain.CoverLetter;
import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.repository.MemberRepository;
import dev.resumate.repository.ResumeRepository;
import dev.resumate.service.TaggingService;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class DummyResume {

    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final TaggingService taggingService;

    public void generate() {
        Faker faker = new Faker(new Locale("ko", "ko"));
        MyCustomFaker myCustomFaker = new MyCustomFaker();

        //500명의 회원이 200개씩 지원서 생성, 지원서에는 자소서가 1개 존재
        for (int i = 4401; i <= 6007; i++) {
            Member member = memberRepository.findById((long) i).orElseThrow(() -> new BusinessBaseException(ErrorCode.MEMBER_NOT_FOUND));

            for (int j = 0; j < 200; j++) {
                //회사, 키워드 데이터 랜덤 생성
                String org = myCustomFaker.resumeFromFile().org();
                String keyword = myCustomFaker.resumeFromFile().keyword();

                CoverLetter coverLetter = CoverLetter.builder()
                        .question(faker.joke().pun() + " " + keyword + "에 대한 질문")
                        .answer(faker.joke().pun() + " " + keyword + " " + faker.joke().pun() + faker.joke().pun() + faker.joke().pun())
                        .build();

                Resume resume = Resume.builder()
                        .member(member)
                        .title(org + " 백엔드 직무")
                        .organization(org)
                        .orgUrl("https://www.datafaker.net/documentation/usage/#default-usage")
                        .applyStart(LocalDate.now())
                        .applyEnd(LocalDate.now())
                        .coverLetters(new ArrayList<>())
                        .attachments(new ArrayList<>())
                        .taggings(new ArrayList<>())
                        .build();

                resume.addCoverLetter(coverLetter);
                Resume savedResume = resumeRepository.save(resume);

                //태그 저장
                List<String> tags = new ArrayList<>();
                tags.add("태그" + j % 10);  //태그0 ~ 태그9로 번갈아가며 태깅
                taggingService.saveTagAndTagging(tags, member, savedResume);
            }
        }
    }
}
