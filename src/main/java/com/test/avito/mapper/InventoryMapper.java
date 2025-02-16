package com.test.avito.mapper;

import com.test.avito.dto.InventoryDto;
import com.test.avito.model.Inventory;
import reactor.core.publisher.Mono;

public interface InventoryMapper {
    Mono<InventoryDto> toInventoryDto(Inventory inventory);
}
