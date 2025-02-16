package com.test.avito.service;

import com.test.avito.exception.MerchNotFoundException;
import com.test.avito.exception.NotEnoughCoinsException;
import com.test.avito.exception.UserNotFoundException;
import com.test.avito.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final UserService userService;

    private final MerchService merchService;

    @Transactional
    public Mono<String> purchaseItem(String userEmail, String itemName) {
        return userService.findByEmail(userEmail)
                .switchIfEmpty(Mono.error(new UnknownError("user not found")))
                .zipWith(merchService.findMerchByName(itemName)
                        .switchIfEmpty(Mono.error(new MerchNotFoundException("Merch not found"))))
                .flatMap(tpl -> {
                        if (tpl.getT1().getCoins() < tpl.getT2().getPrice()) {
                            return Mono.empty();
                        }
                        return userService.buyMerch(tpl.getT1().getId(), tpl.getT2().getId());
                })
                .switchIfEmpty(Mono.error(new NotEnoughCoinsException("Not enough coins")));
    }

    @Transactional
    public Mono<String> sendCoin(String userFirst, String userSecond, long amount) {
        return userService
                .findByEmail(userFirst)
                .switchIfEmpty(Mono.error(new UserNotFoundException("user not found")))
                .mapNotNull(user -> user.getCoins() < amount ? null: user)
                .switchIfEmpty(Mono.error(new NotEnoughCoinsException("Not enough coins")))
                .zipWith(userService.findByEmail(userSecond)
                        .switchIfEmpty(Mono.error(new UserNotFoundException("user not found"))))
                .flatMap(userTuple ->
                        userService.sendMoney(userTuple.getT1().getId(),
                                userTuple.getT2().getId(),
                                amount));
    }

}
