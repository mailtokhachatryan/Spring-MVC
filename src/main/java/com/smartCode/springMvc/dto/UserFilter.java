package com.smartCode.springMvc.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserFilter {

    private Integer startAge;
    private Integer endAge;
    private Boolean isVerified;
    private LocalDateTime startCreatedAt;
    private LocalDateTime endCreatedAt;

}
