package com.example.study.model.network;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Header<T> {    // 계속 바뀌는 data 부분이므로 제네릭 이용

    // api 통신시간
    private LocalDateTime transactionTime;

    // api 응답코드
    private String resultCode;

    // api 부가설명
    private String description;

    private T data;

    // OK   (정상적인 통신일 때는 OK만 호출)
    public static <T> Header<T> OK() {
        return (Header<T>)Header.builder()
                .transactionTime(LocalDateTime.now())
                .resultCode("OK")
                .description("OK")
                .build();
    }

    // DATA OK  (데이터가 있을 때는 DATA OK 호출)
    public static <T> Header<T> OK(T data) {
        return (Header<T>) Header.builder()
                .transactionTime(LocalDateTime.now())
                .resultCode("OK")
                .description("OK")
                .data(data)
                .build();
    }

    // ERROR    (비정상일 때는 ERROR 호출)
    public static <T> Header<T> ERROR(String description) {
        return (Header<T>)Header.builder()
                .transactionTime(LocalDateTime.now())
                .resultCode("ERROR")
                .description(description)   // 어떤 에러인지 프론트엔드에게 알려주기 위해
                .build();
        }

}
