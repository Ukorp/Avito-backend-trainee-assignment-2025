package com.test.avito.controller;

import com.test.avito.dto.SendCoinRequest;
import com.test.avito.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/sendCoin")
    @ResponseBody
    public ResponseEntity<Mono<String>> sendCoin(@Valid @RequestBody SendCoinRequest sendCoinRequest, Principal principal) {
        String principalName = principal.getName();
        log.debug("send money from {} to id {}", principalName, sendCoinRequest.getToUser());
        return ResponseEntity.ok(purchaseService.sendCoin(principalName, sendCoinRequest.getToUser(), sendCoinRequest.getAmount()));
    }

    @PostMapping("/buy/{item}")
    @ResponseBody
    public ResponseEntity<Mono<String>> buyMerch(@Valid @PathVariable("item") String item, Principal principal) {
        String principalName = principal.getName();
        log.debug("buy merch ({}) {}", principalName, item);
        return ResponseEntity.ok(purchaseService.purchaseItem(principalName, item));
    }
}
