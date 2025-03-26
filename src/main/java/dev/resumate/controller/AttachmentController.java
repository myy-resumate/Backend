package dev.resumate.controller;

import dev.resumate.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * 파일 다운로드
     * @param attachmentId
     * @return
     * @throws IOException
     */
    @GetMapping("/{attachmentId}")
    public ResponseEntity<?> fileDownload(@PathVariable(name = "attachmentId") Long attachmentId) throws IOException {
        return attachmentService.fileDownload(attachmentId);
    }
}
