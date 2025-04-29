package dev.resumate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final VectorStore vectorStore;

    //테스트
    public String pineconeTest() {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("resumeId", 1);
        metaData.put("coverLetterId", 23);

        Map<String, Object> metaData2 = new HashMap<>();
        metaData2.put("resumeId", 2);
        metaData2.put("coverLetterId", 45);

        Map<String, Object> metaData3 = new HashMap<>();
        metaData3.put("resumeId", 3);
        metaData3.put("coverLetterId", 32);


        List<Document> documentList = List.of(
                new Document("본인이 진행한 프로젝트 중 가장 기억에 남는 프로젝트를 소개하고, 그 과정에서 맡았던 역할과 해결한 문제를 서술해 주세요.", metaData),
                new Document("새로운 기술이나 도구를 빠르게 학습해야 했던 경험이 있다면, 어떻게 접근하고 적용했는지 구체적으로 설명해 주세요.", metaData2),
                new Document("팀 프로젝트에서 발생한 갈등이나 어려움을 해결한 경험이 있다면, 그 상황과 해결 과정을 설명해 주세요.", metaData3));

        vectorStore.add(documentList);

        //vectorStore.similaritySearch()
        return "벡터 저장 성공";
    }
}
