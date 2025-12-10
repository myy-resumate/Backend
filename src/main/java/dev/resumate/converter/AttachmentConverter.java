package dev.resumate.converter;

import dev.resumate.domain.Attachment;

public class AttachmentConverter {

    public static Attachment toAttachment(String uploadKey, String fileName) {

        return Attachment.builder()
                .uploadKey(uploadKey)
                .fileName(fileName)
                .build();
    }
}
