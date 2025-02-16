package com.test.avito.mapper.impl;

import com.test.avito.dto.InfoResponse;
import com.test.avito.mapper.CoinHistoryMapper;
import com.test.avito.mapper.InfoResponseMapper;
import com.test.avito.mapper.InventoryMapper;
import com.test.avito.model.User;
import com.test.avito.service.InventoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Data
@RequiredArgsConstructor
public class InfoResponseMapperImpl implements InfoResponseMapper {

    private final InventoryService inventoryService;

    private final InventoryMapper inventoryMapper;

    private final CoinHistoryMapper coinHistoryMapper;

    @Override
    public Mono<InfoResponse> toInfoResponse(User user) {
        return inventoryService.findAllByUserId(user.getId())
                .flatMap(inventoryMapper::toInventoryDto)
                .collectList()
                .zipWith(coinHistoryMapper.toCoinHistory(user))
                .map(elem -> new InfoResponse(user.getCoins(), elem.getT1(), elem.getT2()));

    }
}
