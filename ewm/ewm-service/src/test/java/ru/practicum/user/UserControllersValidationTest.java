package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.GlobalExceptionHandler;
import ru.practicum.NotFoundException;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.user.controller.AdminUserController;
import ru.practicum.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminUserController.class, GlobalExceptionHandler.class})
class UserControllersValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        UserDto userDto = new UserDto("user1@example.com", 1L, "user1");
        NewUserDto newUserDto = new NewUserDto("user1", "user1@example.com");

        List<UserDto> userDtoList = Collections.singletonList(userDto);

        Mockito.when(userService.getAll(Collections.emptyList(), 0, 10))
                .thenReturn(userDtoList);

        Mockito.when(userService.getAll(Collections.singletonList(1L), 0, 10))
                .thenReturn(userDtoList);

        Mockito.when(userService.create(any(NewUserDto.class)))
                .thenReturn(userDto);

        Mockito.doNothing().when(userService).delete(1L);
    }

    @Test
    public void testGetUsers() throws Exception {
        // Test fetching all users
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/users")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("user1"))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"));

        // Test fetching specific users by IDs
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/users")
                        .param("ids", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("user1"))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"));

        // Test empty result when no users found
        Mockito.when(userService.getAll(Collections.singletonList(2L), 0, 11))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/users")
                        .param("ids", "2")
                        .param("from", "0")
                        .param("size", "11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testAddUser() throws Exception {
        NewUserDto newUserDto = new NewUserDto("user1@example.com", "user1");
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));

        // Test validation error
        NewUserDto invalidUserDto = new NewUserDto("invalid-email", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/1"))
                .andExpect(status().isNoContent());

        // Test delete non-existing user
        Mockito.doThrow(new NotFoundException("User not found")).when(userService).delete(2L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/2"))
                .andExpect(status().isNotFound());
    }
}