package com.example.study.model.enumclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    // index id값, 프론트엔드에 노출되는 값, 상세한 값
    REGISTERED(0, "등록", "사용자 등록 상태"),
    UNREGISTERED(1, "해지", "사용자 해지 상태")
    ;

    private Integer id;
    private String title;
    private String description;

}
