package dev.resumate.service;

import dev.resumate.domain.CoverLetter;
import dev.resumate.domain.Member;
import dev.resumate.dto.AiRequestDTO;
import dev.resumate.dto.AiResponseDTO;
import dev.resumate.repository.CoverLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AiService {

    private static final int TOP_K = 3;
    private static final double SIMILARITY_THRESHOLD = 0.7;
    private final VectorStore vectorStore;
    private final CoverLetterRepository coverLetterRepository;

    public AiResponseDTO.SimilarQuestionDTO similaritySearch(Member member, AiRequestDTO.QuestionDTO request) {
        System.out.println(request.getQuestion());

        List<Document> similarQuestionList = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(request.getQuestion())
                        .topK(TOP_K)  //유사한 상위 3개
                        .similarityThreshold(SIMILARITY_THRESHOLD)  //최소 score(1이면 완전 일치)
                        .filterExpression("member_id == " + member.getId())  //메타 데이터인 멤버id로 필터링
                        .build()
        );

        //자소서 조회
        List<Long> coverLetterIds = similarQuestionList.stream().map(sq -> metaDataTypeChange(sq, "cover_letter_id")).toList();
        List<CoverLetter> coverLetters = coverLetterRepository.findAllById(coverLetterIds);

        List<AiResponseDTO.QuestionAnswerDTO> questionAnswerDTOList = similarQuestionList.stream().map(sq ->
                AiResponseDTO.QuestionAnswerDTO.builder()
                        .resumeId(metaDataTypeChange(sq, "resume_id"))
                        .question(sq.getText())
                        .answer(matchAnswer(coverLetters, metaDataTypeChange(sq, "cover_letter_id")))
                        .build()).toList();

        return AiResponseDTO.SimilarQuestionDTO.builder()
                .questionAnswerDTOList(questionAnswerDTOList)
                .build();
    }

    //자소서 id에 해당하는 자소서 답변을 반환
    private String matchAnswer(List<CoverLetter> coverLetters, Long coverLetterId) {

        List<CoverLetter> filterCoverLetters = coverLetters.stream()
                .filter(c -> Objects.equals(c.getId(), coverLetterId)).toList();
        return filterCoverLetters.get(0).getAnswer();
    }

    //오브젝트 타입인 메타데이터를 long타입으로 변환
    private Long metaDataTypeChange(Document doc, String metaDataName) {
        return ((Double) doc.getMetadata().get(metaDataName)).longValue();
    }
}
