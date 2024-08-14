package ru.practicum.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.GlobalExceptionHandler;
import ru.practicum.comment.controller.AdminCommentController;
import ru.practicum.comment.controller.PrivateCommentController;
import ru.practicum.comment.controller.PublicCommentController;
import ru.practicum.comment.service.CommentService;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.ShortCommentDto;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {AdminCommentController.class, PrivateCommentController.class, PublicCommentController.class, GlobalExceptionHandler.class})
class CommentControllersValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    public void testDeleteAdminComment() throws Exception {
        // Mock the service call
        Mockito.doNothing().when(commentService).delete(1L);

        // Test the delete comment endpoint
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/comments/1"))
                .andExpect(status().isNoContent());

        // Test for a non-existent comment
        Mockito.doThrow(new NotFoundException("Comment not found")).when(commentService).delete(2L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/comments/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBanUserFromCommenting() throws Exception {
        // Mock the service call
        Mockito.doNothing().when(commentService).banUserFromCommenting(1L, 1L);

        // Test the ban user from commenting endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/comments/ban/1/event/1"))
                .andExpect(status().isOk());

        // Test with an invalid user or event ID
        Mockito.doThrow(new NotFoundException("User or Event not found")).when(commentService).banUserFromCommenting(2L, 2L);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/comments/ban/2/event/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPinComment() throws Exception {
        // Mock the service call
        Mockito.doNothing().when(commentService).pinComment(1L);

        // Test the pin comment endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/comments/pin/1"))
                .andExpect(status().isOk());

        // Test for a non-existent comment
        Mockito.doThrow(new NotFoundException("Comment not found")).when(commentService).pinComment(2L);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/comments/pin/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddComment() throws Exception {
        NewCommentDto newCommentDto = new NewCommentDto(1L, "Test comment");
        ShortCommentDto shortCommentDto = new ShortCommentDto(1L, 1L, LocalDateTime.now(), "Test comment");
        ObjectMapper objectMapper = new ObjectMapper();

        Mockito.when(commentService.create(any(NewCommentDto.class), eq(1L)))
                .thenReturn(shortCommentDto);

        // Test the add comment endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test comment"));

        // Test with invalid input
        NewCommentDto invalidCommentDto = new NewCommentDto(1L, "");

        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEditComment() throws Exception {
        ShortCommentDto updatedCommentDto = new ShortCommentDto(1L, 1L, LocalDateTime.now(), "Updated comment");
        String newText = "Updated comment";

        Mockito.when(commentService.edit(eq(1L), eq(1L), eq(newText)))
                .thenReturn(updatedCommentDto);

        // Test the edit comment endpoint
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Updated comment"));

        // Test with invalid newText (empty string)
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        // Test with invalid newText (too long string)
        String longText = "a".repeat(201);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(longText))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteComment() throws Exception {
        // Mock the service call
        Mockito.doNothing().when(commentService).delete(1L, 1L);

        // Test the delete comment endpoint
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/comments/1"))
                .andExpect(status().isNoContent());

        // Test for a non-existent comment
        Mockito.doThrow(new NotFoundException("Comment not found")).when(commentService).delete(2L, 1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/comments/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetComments() throws Exception {
        List<CommentDto> comments = List.of(
                new CommentDto(1L, 1L, LocalDateTime.now(), null, "First comment", false),
                new CommentDto(2L, 1L, LocalDateTime.now(), null, "Second comment", false)
        );

        Mockito.when(commentService.getCommentsByPostId(eq(1L), eq(0), eq(10), eq("DESC")))
                .thenReturn(comments);

        // Test the get comments endpoint
        mockMvc.perform(MockMvcRequestBuilders.get("/comments")
                        .param("eventId", "1")
                        .param("from", "0")
                        .param("size", "10")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].text").value("First comment"));

        // Test with invalid parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/comments")
                        .param("eventId", "1")
                        .param("from", "-1")
                        .param("size", "10")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/comments")
                        .param("eventId", "1")
                        .param("from", "0")
                        .param("size", "0")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetComment() throws Exception {
        CommentDto comment = new CommentDto(1L, 1L, LocalDateTime.now(), null, "Test comment", false);

        Mockito.when(commentService.getById(1L)).thenReturn(comment);

        // Test the get comment by ID endpoint
        mockMvc.perform(MockMvcRequestBuilders.get("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test comment"));

        // Test for a non-existent comment
        Mockito.when(commentService.getById(2L)).thenThrow(new NotFoundException("Comment not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/comments/2"))
                .andExpect(status().isNotFound());
    }
}