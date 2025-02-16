package com.test.avito.mapper.impl;

import com.test.avito.dto.InventoryDto;
import com.test.avito.mapper.InventoryMapper;
import com.test.avito.model.Inventory;
import com.test.avito.service.InventoryService;
import com.test.avito.service.MerchService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Data
@RequiredArgsConstructor
@Component
public class InventoryMapperImpl implements InventoryMapper {

    private final MerchService merchService;

    private final InventoryService inventoryService;

    @Override
    public Mono<InventoryDto>  toInventoryDto(Inventory inventory) {
        return merchService
                .findMerchById(inventory.getItemId())
                .map(merch -> new InventoryDto(merch.getName(), inventory.getQuantity()));
    }
}
