package com.app.web.dto;

import com.app.data.entity.Order;
import com.app.data.entity.enums.Payment;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class OrderDto {

    private String item;

    private int price;

    private Date date;

    private String clientName;

    private String sellerName;

    private Payment paymentMethod;

    public static OrderDto from(Order order) {
        return OrderDto.builder()
                .item(order.getItem().getName())
                .price(order.getItem().getPrice())
                .date(order.getDate())
                .paymentMethod(order.getPaymentMethod())
                .clientName(order.getClient().getFirstName() + " " + order.getClient().getLastName())
                .sellerName(order.getItem().getSeller().getFirstName() +
                        " " + order.getItem().getSeller().getLastName())
                .build();
    }
}
