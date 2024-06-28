package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
private DataSource dataSource;
@Autowired
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
    List<Category> categoryList = new ArrayList<>();
        try(Connection connection = getConnection()){
            String query = """
                    SELECT category_id
                                   ,name
                                   ,description
                                   FROM categories;
                    """;
            Statement statement = connection.createStatement();

            ResultSet set = statement.executeQuery(query);
            while(set.next()){
            int categoryID = set.getInt("category_id");
            String name = set.getString("name");
            String description = set.getString("description");
            Category category = new Category(categoryID, name, description);
            categoryList.add(category);
            }


        } catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
        // get all categories
        return categoryList;
    }

    @Override
    public Category getById(int categoryId)
    {
        Category category = new Category();
        try(Connection connection = getConnection()){
            String query = """
                    SELECT category_id
                     , name 
                     , description 
                     FROM Categories where category_id = ?
                    """;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, categoryId);

            ResultSet set = statement.executeQuery();
            if(set.next()){
                return mapRow(set);
            }


        } catch (Exception e){
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
return category;
    }

    @Override
    public Category create(Category category)
    {

        // create a new category
        try(Connection connection = getConnection()){
            String query = """
                    INSERT INTO categories(category_id, name, description)
                    VALUES(?,?,?)
                    """;
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1,category.getCategoryId());
            statement.setString(2, category.getName());
            statement.setString(3, category.getDescription());
            statement.executeUpdate();

            // this is to get the new autonumber that was inserted
            ResultSet keyRow = statement.getGeneratedKeys();
            keyRow.next();
            int newId = keyRow.getInt(1);

            category.setCategoryId(newId);
            return category;

        } catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }

        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        try(Connection connection = getConnection()){
            String query =  """
            UPDATE categories
            SET name = ?
            ,description = ?
            WHERE category_id = ?;
            """;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, categoryId);

            preparedStatement.executeUpdate();
}catch (Exception e){
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
}

}

    @Override
    public void delete(int categoryId)
    {
try(Connection connection = getConnection()){
    String query = """
                    DELETE FROM categories WHERE category_id = ?
                    """;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, categoryId);
            statement.executeUpdate();
            

        } catch (Exception e){
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
