package dev.resumate.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of(
                        "https://hammanger.pages.dev",
                        "http://localhost:3000"
                )
        );
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);  //true면 addAllowedOriginPattern("*") 사용 불가 -> 도메인 명시해야함

        //파일 다운로드 시 cors 문제로 content-disposition이 안 담겨오는 문제 해결
        //exposeHeader에 content-disposition 추가
        List<String> exposeHeaders = new ArrayList<>();
        exposeHeaders.add("Content-Disposition");
        configuration.setExposedHeaders(exposeHeaders);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
