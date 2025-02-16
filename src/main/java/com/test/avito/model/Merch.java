package com.test.avito.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("merch")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merch {
    @Id
    @Column("id")
    @JsonIgnore
    private long id;

    @Column("name")
    private String name;

    @Column("price")
    private long price;
}
