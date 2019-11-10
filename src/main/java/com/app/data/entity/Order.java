package com.app.data.entity;

import com.app.data.entity.enums.Payment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@Builder
@Entity
@Table(name = "item_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private Payment paymentMethod;

    @Column(name = "date")
    private Date date;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", unique = true)
    @JsonIgnore
    private Item item;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;
}
