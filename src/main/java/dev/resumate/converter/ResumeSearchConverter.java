package dev.resumate.converter;

import dev.resumate.domain.Resume;
import dev.resumate.domain.ResumeSearch;

public class ResumeSearchConverter {

    public static ResumeSearch toResumeSearch(Resume resume, String questions, String answers) {
        return ResumeSearch.builder()
                .title(resume.getTitle())
                .organization(resume.getOrganization())
                .questions(questions)
                .answers(answers)
                .build();
    }
}
