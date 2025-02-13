package dev.resumate.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachmentDTO {

    private String fileName;
    private String url;
}
