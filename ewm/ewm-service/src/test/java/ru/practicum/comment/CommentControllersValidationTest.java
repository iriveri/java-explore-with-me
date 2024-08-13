package ru.practicum.comment;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.practicum.GlobalExceptionHandler;
import ru.practicum.category.controller.AdminCategoryController;
import ru.practicum.category.controller.PublicCategoryController;

@WebMvcTest(controllers = {AdminCategoryController.class, PublicCategoryController.class, GlobalExceptionHandler.class})
public class CommentControllersValidationTest {
}
