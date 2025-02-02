package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping(value = "/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    // a shopping cart requires

    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;
@Autowired
public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao){
    this.shoppingCartDao = shoppingCartDao;
    this.userDao = userDao;
    this.productDao = productDao;
}


    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try {
            ShoppingCart shoppingCart = new ShoppingCart();
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            shoppingCart = shoppingCartDao.getByUserId(userId);
            return shoppingCart;
        } catch (Exception e){
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    //
    @PostMapping("products/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ShoppingCart addToCart(Principal principal, @PathVariable int id){
    ShoppingCart shoppingCart = new ShoppingCart();
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            Product product= productDao.getById(id);
            ShoppingCartItem item = new ShoppingCartItem();
            item.setProduct(product);
            shoppingCartDao.add(item,userId);


        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
return shoppingCart;

    }

    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
@PutMapping({"products/{productId}"})
@ResponseStatus(HttpStatus.OK)
public void update(Principal principal, @PathVariable int productId, @RequestBody ShoppingCartItem item) {
    try {
        String userName = principal.getName();
        // find database user by userId
        User user = userDao.getByUserName(userName);
        int userId = user.getId();
        shoppingCartDao.update(productDao.getById(productId));
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    @DeleteMapping
    public ShoppingCart emptyCart(Principal principal){
        String userName = principal.getName();
        // find database user by userId
        User user = userDao.getByUserName(userName);
        int userId = user.getId();
        shoppingCartDao.empty(userId);
        return shoppingCartDao.getByUserId(userId);

    }

}
