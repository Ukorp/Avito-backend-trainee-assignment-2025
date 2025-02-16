package com.test.avito.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoinReceived {
    private String fromUser;
    private int amount;
}
