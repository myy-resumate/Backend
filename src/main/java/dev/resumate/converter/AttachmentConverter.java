package dev.resumate.converter;

import dev.resumate.domain.Attachment;

public class AttachmentConverter {

    public static Attachment toAttachment(String url, String uploadKey, String fileName) {

        return Attachment.builder()
                .url(url)
                .uploadKey(uploadKey)
                .fileName(fileName)
                .build();
    }
}
