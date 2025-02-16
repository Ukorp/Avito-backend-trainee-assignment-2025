package com.test.avito.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @JsonIgnore
    @Column("id")
    private long id;

    @Column("item_id")
    private long itemId;

    @Column("user_id")
    private long userId;

    @Column("quantity")
    private int quantity;
}
