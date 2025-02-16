package com.test.avito.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CoinHistory {
    private List<CoinReceived> received;
    private List<CoinSent> sent;
}
