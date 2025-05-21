package ehcache.example.ehCache.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ehcache.example.ehCache.Dto.UserDto;
import ehcache.example.ehCache.Dto.CreateUserDto;
import ehcache.example.ehCache.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@TestPropertySource(properties = "spring.security.enabled=false")
@AutoConfigureMockMvc(addFilters = false)

public class UserControllerTest {

    private static final String BASE_URL ="/api/C2/user" ;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateUserDto createUserDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        createUserDto = CreateUserDto.builder()
                .username("johndoe")
                .password("Secure123!#qf")
                .name("JohnDoe")
                .email("john@example.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("johndoe")
                .name("JohnDoe")
                .email("john@example.com")
                .build();
    }

    @Test
    void createUser_ValidInput_ReturnsCreatedUser() throws Exception {
        when(userService.createNewUser(any(CreateUserDto.class))).thenReturn(userDto);

        mockMvc.perform(post(BASE_URL + "/createNewUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));



    }



    @Test
    void getUserById_ValidId_ReturnsUser() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get(BASE_URL + "/getUserById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("JohnDoe"));
    }

    @Test
    void getAllUsers_ReturnsUserList() throws Exception {
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .username("janedoe")
                .name("JaneDoe")
                .build();

        when(userService.getAllUsers()).thenReturn(List.of(userDto, userDto2));

        mockMvc.perform(get(BASE_URL + "/getAllUsers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[1].username").value("janedoe"));
    }

    @Test
    void updateUser_ValidInput_ReturnsUpdatedUser() throws Exception {
        UserDto updatedDto = UserDto.builder()
                .name("John Updated")
                .id(1L)
                .email("updated@example.com")
                .username("darkyiwnl123")
                .build();

        when(userService.updateUser(any(UserDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put(BASE_URL + "/updateUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));

        ArgumentCaptor<UserDto> dtoCaptor = ArgumentCaptor.forClass(UserDto.class);
        verify(userService).updateUser( dtoCaptor.capture());
        assertThat(dtoCaptor.getValue().getName()).isEqualTo("John Updated");
    }

    @Test
    void deleteUser_ValidId_ReturnsConfirmation() throws Exception {
        when(userService.remUser(anyLong())).thenReturn("User deleted");

        mockMvc.perform(delete(BASE_URL + "/remUser/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("user removed"));
    }

    @Test
    void getUserByUsername_ValidUsername_ReturnsUser() throws Exception {
        when(userService.getUserByUsername(anyString())).thenReturn(userDto);

        mockMvc.perform(get(BASE_URL + "/getUserByUsername/{username}", "john_doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"));
    }
}