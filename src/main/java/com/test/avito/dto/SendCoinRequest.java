package com.test.avito.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendCoinRequest {
    @JsonProperty("toUser")
    private String toUser;
    @JsonProperty("amount")
    private long amount;
}
