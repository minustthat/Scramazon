package org.yearup.data.mysql;

import org.apache.tomcat.util.digester.RuleSet;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
@Component
public class MySqlShoppingCartDAO extends MySqlDaoBase implements ShoppingCartDao {
    private ProductDao productDao;
    public MySqlShoppingCartDAO(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {

        ShoppingCart shoppingCart = new ShoppingCart();
        try(Connection connection = getConnection()){
            String query = """
                    SELECT user_id
                            , product_id
                            ,quantity
                    FROM shopping_cart where user_id = ?     
                    """;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1,userId);
            ResultSet row = statement.executeQuery();

            if(row.next())
            {
                int userID = row.getInt("user_id");
                int productid= row.getInt("product_id");
                int quantity = row.getInt("quantity");

                Product product = productDao.getById(productid);
                ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                shoppingCartItem.setProduct(product);
                shoppingCartItem.setQuantity(quantity);
                shoppingCart.add(shoppingCartItem);
            }
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
        return  shoppingCart;
    }

    @Override
    public ShoppingCart add(ShoppingCartItem shoppingCartItem, int userid) {
        ShoppingCart shoppingCart = new ShoppingCart();
        try(Connection connection = getConnection()){
            String query = """
                    INSERT INTO shopping_cart (user_id, product_id, quantity)
                    VALUES(?,?,?)
                    """;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userid);
            statement.setInt(2, shoppingCartItem.getProductId());
            statement.setInt(3, shoppingCartItem.getQuantity());
            ResultSet row = statement.executeQuery();
            while(row.next()){
                int userId = row.getInt("user_id");
                int productid = row.getInt("product_id");
                int quantity = row.getInt("quantity");
                Product product = productDao.getById(userid);
                shoppingCartItem.setProduct(product);
                shoppingCart.add(shoppingCartItem);
                quantity++;
            }

            statement.executeUpdate();

        }catch (Exception e){
throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        return shoppingCart;
    }

    @Override
    public void update(Product product) {

    }

    @Override
    public ShoppingCart empty(int userid) {
        try(Connection connection = getConnection()){
            ShoppingCart shoppingCart;
            String query = """
                    DELETE FROM shopping_cart where user_id = ?
                   """;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userid);
            statement.executeUpdate();


        }catch (Exception e){


        }
return null;
    }


}
