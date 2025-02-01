package dev.resumate.service;

import dev.resumate.domain.Attachment;
import dev.resumate.domain.Resume;
import dev.resumate.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    public void saveAttachment(String url, Resume resume) {

        Attachment attachment = Attachment.builder()
                .url(url)
                .resume(resume)
                .build();

        attachmentRepository.save(attachment);
    }
}
