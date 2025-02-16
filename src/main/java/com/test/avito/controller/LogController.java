package com.test.avito.controller;

import com.test.avito.model.Logs;
import com.test.avito.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class LogController {

    private final LogService logService;

    @GetMapping("/logs")
    @ResponseBody
    public ResponseEntity<Flux<Logs>> getLogs(Principal principal) {
        String principalName = principal.getName();
        return ResponseEntity.ok(logService.FindAllLogsByUsername(principalName)
        );
    }
}
