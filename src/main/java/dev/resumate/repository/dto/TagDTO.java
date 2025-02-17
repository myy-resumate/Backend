package dev.resumate.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor  //역직렬화를 하기 위해 기본 생성자 필요
@AllArgsConstructor
public class TagDTO {

    private String tagName;

}
