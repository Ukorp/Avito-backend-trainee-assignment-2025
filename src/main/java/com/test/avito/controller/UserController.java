package com.test.avito.controller;

import com.test.avito.dto.ErrorResponse;
import com.test.avito.dto.InfoResponse;
import com.test.avito.exception.UserNotFoundException;
import com.test.avito.mapper.InfoResponseMapper;
import com.test.avito.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final InfoResponseMapper infoResponseMapper;

    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<Mono<InfoResponse>> getInfo(Principal principal) {
        String principalName = principal.getName();
        return ResponseEntity.ok(userService.findByEmail(principalName)
                .switchIfEmpty(Mono.error(new UserNotFoundException("user not found")))
                .flatMap(infoResponseMapper::toInfoResponse));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

}
