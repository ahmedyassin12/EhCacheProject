package ehcache.example.ehCache.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ehcache.example.ehCache.Dto.AdminDto;
import ehcache.example.ehCache.Dto.CreateAdminDto;
import ehcache.example.ehCache.Service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

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
    void getAllAdmins() {
    }

    @Test
    void getAdminById() {
    }

    @Test
    void AdminController_createNewAdmin_ReturnAdmin()throws Exception {


        given(adminService.createNewAdmin(ArgumentMatchers.any()))
                .willAnswer((invocation->invocation.getArguments()));

        ResultActions response =mockMvc.perform((RequestBuilder) post("/createNewAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.valueOf(objectMapper.writeValueAsString(adminDto)))
        ) ;

        response.andExpect(MockMvcResultMatchers.status().isCreated()) ;



    }

    @Test
    void getManagerByName() {
    }

    @Test
    void getAdminByUsername() {
    }

    @Test
    void remAdmin() {
    }

    @Test
    void update_admin() {
    }
}