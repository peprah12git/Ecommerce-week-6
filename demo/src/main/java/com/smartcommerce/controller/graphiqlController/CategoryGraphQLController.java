package com.smartcommerce.controller.graphiqlController;

import com.smartcommerce.model.Category;
import com.smartcommerce.service.imp.CategoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Controller for Category operations
 * Handles GraphQL queries and mutations for categories
 * Coexists with REST endpoints in CategoryController
 */
@Controller
public class CategoryGraphQLController {

    private final CategoryService categoryService;

    public CategoryGraphQLController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Get a single category by ID
     * GraphQL Query: category(id: Int!): Category
     */
    @QueryMapping
    public Category category(@Argument int id) {
        return categoryService.getCategoryById(id);
    }

    /**
     * Get all categories
     * GraphQL Query: categories: [Category!]!
     */
    @QueryMapping
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }

}
