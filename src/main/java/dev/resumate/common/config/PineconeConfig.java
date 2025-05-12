package dev.resumate.common.config;

import dev.resumate.common.embedding.UpstageEmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class PineconeConfig {

    private final RestTemplate restTemplate;

    @Bean
    public EmbeddingModel embeddingModel() {
        return new UpstageEmbeddingModel(restTemplate);
    }
}
