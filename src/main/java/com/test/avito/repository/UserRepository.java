package com.test.avito.repository;

import com.test.avito.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByUsername(String username);

    @Query("select * from transfer_money(:sender, :receiver, :amount, :description)")
    Mono<String> sendMoney(@Param("sender") long senderId,
                         @Param("receiver") long receiverId,
                         @Param("amount") long amount,
                         @Param("description") String description);

    @Query("select * from buy_merch(:user_id, :merch_id)")
    Mono<String> buyMerch(@Param("user_id") long userId,
                           @Param("merch_id") long merchId);



}
