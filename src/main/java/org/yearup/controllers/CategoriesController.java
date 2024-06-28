package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.data.mysql.MySqlCategoryDao;
import org.yearup.models.Category;
import org.yearup.models.Product;
import org.yearup.models.Profile;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/categories")


public class CategoriesController
{

    private CategoryDao categoryDao;
    private ProductDao productDao;



    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao){
    this.categoryDao = categoryDao;
    this.productDao = productDao;
}
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Category> getAll()
    {
    List <Category> categories = categoryDao.getAllCategories();
        // find and return all categories
      return categories;
    }


    @GetMapping({"/{id}"})
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id)
    {
        Category category = categoryDao.getById(id);

        return category;

    }


    // https://localhost:8080/categories/1/products
    @GetMapping({"{categoryId}/products"})
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        List<Product> products = productDao.listByCategoryId(categoryId);
        return products;

    }
@PostMapping
@PreAuthorize("hasRole('ROLE_ADMIN')")
@ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category)
    {

    Category newCategory = categoryDao.create(category);
    return newCategory;

    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {
categoryDao.update(id, category);

    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("/{id}")
     @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id)
    {
        categoryDao.delete(id);

    }
}
