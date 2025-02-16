package com.test.avito.controller;

import com.test.avito.model.Inventory;
import com.test.avito.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.security.Principal;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/inventory")
    @ResponseBody
    public ResponseEntity<Flux<Inventory>> getInventory(Principal principal) {
        String principalName = principal.getName();
        return ResponseEntity.ok(inventoryService.getUserInventory(principalName));
    }
}
