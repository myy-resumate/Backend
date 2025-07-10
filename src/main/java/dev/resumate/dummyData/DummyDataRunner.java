package dev.resumate.dummyData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "dummy.enabled", havingValue = "true")  //yml파일에서 true로 설정했을 때만 실행됨
@Slf4j
public class DummyDataRunner implements CommandLineRunner {

    private final DummyMember dummyMember;
    private final DummyResume dummyResume;

    @Override
    public void run(String... args) throws Exception {
        log.info("더미 데이터 생성");
        //dummyMember.generate();  //멤버 데이터 500개 생성
        //dummyResume.generate();  //지원서 데이터 10만 개 생성
    }
}
