package com.app.web.dto;

import com.app.data.entity.Item;
import com.app.data.entity.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {

    private String name;

    private int price;

    private String description;

    private Status status;

    private String seller;

    public static ItemDto from(Item item) {
        return ItemDto.builder()
                .name(item.getName())
                .price(item.getPrice())
                .description(item.getDescription())
                .status(item.getStatus())
                .seller(item.getSeller().getFirstName() + " " + item.getSeller().getLastName())
                .build();
    }
}
