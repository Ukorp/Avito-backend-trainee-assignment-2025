package com.test.avito.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("transaction_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Logs {


    @Id
    @JsonIgnore
    @Column("id")
    private long id;

    @Column("user_id")
    private long userId;

    @Column("details")
    private String details;

    @Column("coins")
    private int coins;

    @Column("description")
    private String description;

    @Column("date")
    private LocalDateTime date;

    @Column("second_user_id")
    private long secondUserId;
}
