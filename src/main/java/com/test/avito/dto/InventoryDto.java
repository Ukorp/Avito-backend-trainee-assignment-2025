package com.test.avito.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {
    @JsonPropertyDescription("Тип предмета.")
    private String type;

    @JsonPropertyDescription("Количество предметов")
    private int quantity;
}
