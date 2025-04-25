package ehcache.example.ehCache.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import ehcache.example.ehCache.Dto.AdminDto;
import ehcache.example.ehCache.Dto.CreateAdminDto;
import ehcache.example.ehCache.Service.AdminService;
import jdk.jshell.Snippet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService ;


    @Autowired
    private ObjectMapper objectMapper ;

    private CreateAdminDto createAdminDto ;

    private AdminDto adminDto ;




    @BeforeEach
    void setUp() {
        createAdminDto=CreateAdminDto.builder().username("haha").password("secret").name("kopa").build() ;

        adminDto=AdminDto.builder().name("helo").username("helohelo123").build();

    }

    @Test
    void AdminController_getAllAdmins_returnAllAdmin() throws Exception {
        // Arrange
        AdminDto adminDto2 = AdminDto.builder()
                .name("two")
                .username("heloheloTwo22")
                .build();

        when(adminService.getAllAdmins()).thenReturn(List.of(adminDto2, adminDto));

        // Act
        MvcResult mvcResult = mockMvc.perform(
                get("/getAllAdmins")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
               .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        List<AdminDto> responseList = objectMapper.readValue(
                jsonResponse,
                new TypeReference<List<AdminDto>>() {}
        );

        // Assert
        assertThat(responseList).hasSize(2);

        assertThat(responseList.get(0).getName()).isEqualTo(adminDto2.getName());
        assertThat(responseList.get(0).getUsername()).isEqualTo(adminDto2.getUsername());

        assertThat(responseList.get(1).getName()).isEqualTo(adminDto.getName());
        assertThat(responseList.get(1).getUsername()).isEqualTo(adminDto.getUsername());

    }

    @Test
    void AdminController_createAdmin_ReturnAdminDto() throws Exception {
        // Given
        CreateAdminDto createAdminDto = CreateAdminDto.builder()
                .name("John")
                .username("john123")
                .build();

        AdminDto adminDto = AdminDto.builder()
                .name("John")
                .username("john123")
                .build();

        when(adminService.createNewAdmin(ArgumentMatchers.any())).thenReturn(adminDto);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/createNewAdmin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAdminDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(adminDto.getName()))
                .andExpect(jsonPath("$.username").value(adminDto.getUsername()));


    }


    @Test
    void AdminController_getAdminById_ReturnSAdminDto()throws Exception {

        //arrange
        when(adminService.getAdminById(ArgumentMatchers.anyLong())).thenReturn(adminDto);

        //act + Assert
        mockMvc.perform(get("/getAdminById/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value(adminDto.getName()))
                .andExpect(jsonPath("$.username").value(adminDto.getUsername())  );


        //verify adminService was called
        verify(adminService).getAdminById(1l);



    }




    @Test
    void AdminController_getAdminByName_ReturnsAdminDto() throws Exception {
        //arrange
        when(adminService.getAdminByname(adminDto.getName())).thenReturn(adminDto);

        //act + Assert
        mockMvc.perform(get("/getAdminByName/{name}",adminDto.getName()) )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value(adminDto.getName()))
                .andExpect(jsonPath("$.username").value(adminDto.getUsername())  );


        //verify adminService was called
        verify(adminService).getAdminByname(adminDto.getName());

    }

    @Test
    void getAdminByUsername() throws Exception {

        //arrange
        when(adminService.getAdminByUsername(adminDto.getUsername())).thenReturn(adminDto);

        //act + Assert
        mockMvc.perform(get("/getAdminByUsername/{username}",adminDto.getUsername()) )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.name").value(adminDto.getName()))
                .andExpect(jsonPath("$.username").value(adminDto.getUsername())  );


        //verify adminService was called
        verify(adminService).getAdminByUsername(adminDto.getUsername());

    }


    @Test
    void remAdmin() throws Exception {
        //arrange
        when(adminService.remAdmin(1l)).thenReturn(null);

        //act + Assert
        mockMvc.perform(delete("/remAdmin/{id}",1l ) )
                .andExpect(MockMvcResultMatchers.status().isOk())
                ;


        //verify adminService was called
        verify(adminService).remAdmin(1l);
        verify(adminService, never()).getAdminById(any()); // Verify no extra calls

    }
    @Test
    void updateAdmin_ValidInput_ReturnsUpdatedAdmin() throws Exception {
        // Arrange
        Long adminId = 1L;
        AdminDto updateDto = AdminDto.builder()
                .name("Updated Name")
                .username("updated_user")
                .build();


        when(adminService.updateAdmin(eq(adminId), any(AdminDto.class)))
                .thenReturn(updateDto);

        // Act & Assert
        mockMvc.perform(patch("/admins/{id}", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.username").value("updated_user"));

        // Verify with argument captor
        ArgumentCaptor<AdminDto> dtoCaptor = ArgumentCaptor.forClass(AdminDto.class);
        //while it is checking adminService it capture our second argument with type adminDto
        verify(adminService).updateAdmin(eq(adminId), dtoCaptor.capture());
        assertThat(dtoCaptor.getValue().getName()).isEqualTo("Updated Name");

    }




}