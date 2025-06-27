package ru.ivanov.cartservice.mapper;

import org.springframework.stereotype.Service;
import ru.ivanov.cartservice.dto.CartDto;
import ru.ivanov.cartservice.model.Cart;

@Service
public class CartMapper {

    public CartDto toDto(Cart cart) {
        return new CartDto(cart.getId(), cart.getUserId());
    }
}
