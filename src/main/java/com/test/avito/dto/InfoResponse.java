package com.test.avito.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoResponse {
    @JsonPropertyDescription("Количество доступных монет.")
    private int coins;
    private List<InventoryDto> inventory;
    private CoinHistory coinHistory;
}
