package dev.resumate.dummyData;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Locale;

@Slf4j
public class ResumeFromFile extends AbstractProvider<BaseProviders> {
    private static final String KEY = "resumesfromfile";

    public ResumeFromFile(BaseProviders faker) {
        super(faker);
        try {
            faker.addPath(Locale.ENGLISH, Paths.get(getClass().getClassLoader().getResource("application-dummy.yml").toURI()));
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 회사 이름을 랜덤하게 반환하는 함수
     * @return
     */
    public String org() {
        return resolve(KEY + ".org");
    }

    /**
     * 자소서 키워드를 랜덤하게 반환하는 함수
     * @return
     */
    public String keyword() {
        return resolve(KEY + ".keyword");
    }
}
