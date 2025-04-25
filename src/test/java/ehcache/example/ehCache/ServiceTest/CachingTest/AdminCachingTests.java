package ehcache.example.ehCache.ServiceTest.CachingTest;

import ehcache.example.ehCache.Dao.AdminDao;
import ehcache.example.ehCache.Dto.AdminDto;
import ehcache.example.ehCache.Entity.Admin;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Service.AdminService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.cache.CacheManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminCachingTests {



    @MockBean
    AdminDao adminDao ;
    @Autowired
    AdminService adminService;



     @Autowired
    private CacheManager cacheManager;

    private Long IdAdmin;
    private Admin admin;

    @BeforeEach
    void setUp() {
        IdAdmin=1l ;
        admin=Admin.builder().id(IdAdmin).name("king")
                .username("kids").password("idk")
                .build();
        when(adminDao.findById(IdAdmin)).thenReturn(Optional.ofNullable(admin));

        // Clear cache before each test
        cacheManager.getCache("admins").clear();
    }

    @Test
    public void AdminService_getAllAdminsCache_RetrurnsAllAdminsDto()throws Exception {

        Admin admin2=Admin.builder().id(2).name("woha")
                .username("+18").password("secret")
                .build();

        Mockito.when(adminDao.findAll()).thenReturn(List.of(admin,admin2)) ;

        List <AdminDto> adminDtos =adminService.getAllAdmins() ;


        List<AdminDto> adminFromCache =adminService.getAllAdmins();



        verify(adminDao, times(1)).findAll();

        Assertions.assertThat(adminDtos.size()).isEqualTo(adminFromCache.size());






    }
    @Test
    public void AdminService_GetAdminByIDCache_returnAdminDto(){


        //Act
        AdminDto admin1=adminService.getAdminById(IdAdmin);

        AdminDto admin2=adminService.getAdminById(IdAdmin);

        //Assert

        verify(adminDao, times(1)).findById(IdAdmin);

        Assertions.assertThat(admin1).isEqualTo(admin2);




    }


    @Test
    public void AdminService_UpdateAdminCache_returnAdmin(){


        //Continue Arrangement

        when(adminDao.save(Mockito.any(Admin.class))).thenReturn(admin) ;

        //Act
        AdminDto admin1=adminService.getAdminById(IdAdmin);

        admin1.setName("kawther");
        admin1.setUsername("lecringe");

        AdminDto updatedadmin=adminService.updateAdmin(IdAdmin,admin1);

        AdminDto admin2=adminService.getAdminById(IdAdmin);

        //Assert
        AdminDto cached = (AdminDto) cacheManager.getCache("admins").get(IdAdmin);

        Assertions.assertThat(cached.getName()).isEqualTo("kawther");

        //since UpdateAdmin method uses findById(because of the implementation of DTO)
       verify(adminDao, times(2)).findById(IdAdmin);



        Assertions.assertThat(updatedadmin.getName()).isEqualTo(admin2.getName()) ;
        Assertions.assertThat(updatedadmin.getUsername()).isEqualTo(admin2.getUsername()) ;




    }

    @Test
    public void AdminService_DeleteAdminCache_returnNull(){

        // Setup
        when(adminDao.findById(IdAdmin)).thenReturn(Optional.of(admin));

        // Prime cache
        adminService.getAdminById(IdAdmin);

        // Verify deletion
        adminService.remAdmin(IdAdmin);
        verify(adminDao).deleteById(IdAdmin);

        // Verify cache was cleared
        assertThat(cacheManager.getCache("admins").get(IdAdmin)).isNull();

        // Subsequent call should hit DB again
        adminService.getAdminById(IdAdmin);
        verify(adminDao, times(2)).findById(IdAdmin);


    }





}
