package org.yearup.data;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.security.Principal;
import java.util.List;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart add(ShoppingCartItem shoppingCartItem, int userid);
    void update(Product product);
    ShoppingCart empty(int userid);
}
