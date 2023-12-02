package com.example.springrest.category;

import com.example.springrest.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(CategoryController.class)
@ContextConfiguration
@Import({SecurityConfig.class})
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.clearContext();
    }
    @Test
    @WithMockUser
    public void testGetCategories() throws Exception {
        List<Category> categories = Arrays.asList(
                new Category("Category1", "👁️"),
                new Category("Category2", "👁️")
        );

        given(categoryService.findAll()).willReturn(categories);

        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Category1")))
                .andExpect(jsonPath("$[1].name", is("Category2")));
    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    public void testCreateCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto(1L,"New Category", "🍕", LocalDateTime.now(), LocalDateTime.now());

        Category createdCategory = new Category();
        createdCategory.setName("New Category");

        given(categoryService.createNew(any(CategoryDto.class))).willReturn(createdCategory);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Category")));
    }

    @Test
    public void testCreateCategoryWithUserNotPermitted() throws Exception {
        CategoryDto categoryDto = new CategoryDto(1L,"New Category", "🍕", LocalDateTime.now(), LocalDateTime.now());

        Category createdCategory = new Category();
        createdCategory.setName("New Category");

        given(categoryService.createNew(any(CategoryDto.class))).willReturn(createdCategory);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isUnauthorized());
    }

}
