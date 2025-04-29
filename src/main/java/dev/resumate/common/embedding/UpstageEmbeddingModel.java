package dev.resumate.common.embedding;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UpstageEmbeddingModel implements EmbeddingModel {

    private final RestTemplate restTemplate;

    @Value("${upstage.url}")
    private String apiUrl;

    @Value("${upstage.api-key}")
    private String apiKey;

    @Value("${upstage.model}")
    private String model;

    @Override  //여러 문장들에 대해 임베딩
    public EmbeddingResponse call(EmbeddingRequest request) {

        //헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        //바디 생성
        EmbeddingRequestBody body = new EmbeddingRequestBody();
        body.setModel(model);
        body.setInput(request.getInstructions());

        HttpEntity<EmbeddingRequestBody> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<UpstageEmbeddingResponse> response = restTemplate.postForEntity(apiUrl, httpEntity, UpstageEmbeddingResponse.class);

        //응답 변환
        List<Embedding> embeddings = new ArrayList<>();

        List<UpstageEmbeddingResponse.DataBlock> dataBlocks = response.getBody().getData();
        for (int i = 0; i < dataBlocks.size(); i++) {
            UpstageEmbeddingResponse.DataBlock dataBlock = dataBlocks.get(i);

            List<Double> doubles = dataBlock.getEmbedding();
            float[] floats = new float[doubles.size()];
            for (int j = 0; j < doubles.size(); j++) {
                floats[j] = doubles.get(j).floatValue();
            }

            embeddings.add(new Embedding(floats, i)); // i가 index
        }
        return new EmbeddingResponse(embeddings);
    }

    @Override  //단건 문장을 임베딩
    public float[] embed(Document document) {
        //헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 문서에서 텍스트 꺼내기
        List<String> text = new ArrayList<>();
        text.add(document.getText());

        // Upstage API 요청 본문 만들기
        EmbeddingRequestBody body = new EmbeddingRequestBody();
        body.setInput(text);
        body.setModel(model);

        // 요청 보내기
        HttpEntity<EmbeddingRequestBody> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<UpstageEmbeddingResponse> response = restTemplate.postForEntity(apiUrl, httpEntity, UpstageEmbeddingResponse.class);

        if (response.getBody() == null || response.getBody().getData().isEmpty()) {
            throw new IllegalStateException("No embedding returned from Upstage");
        }

        // 첫 번째 임베딩 결과 가져오기
        List<Double> doubles = response.getBody().getData().get(0).getEmbedding();
        float[] floats = new float[doubles.size()];
        for (int i = 0; i < doubles.size(); i++) {
            floats[i] = doubles.get(i).floatValue();
        }

        return new float[0];
    }

    @Data
    static class EmbeddingRequestBody {
        private String model;
        private List<String> input;
    }

    @Data
    static class UpstageEmbeddingResponse {
        private List<DataBlock> data;

        @Data
        static class DataBlock {
            private List<Double> embedding;
        }
    }
}
