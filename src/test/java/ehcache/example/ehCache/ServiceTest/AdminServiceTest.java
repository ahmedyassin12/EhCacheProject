package ehcache.example.ehCache.ServiceTest;

import ehcache.example.ehCache.Dao.AdminDao;
import ehcache.example.ehCache.Dto.AdminDto;
import ehcache.example.ehCache.Dto.CreateAdminDto;
import ehcache.example.ehCache.Entity.Admin;
import ehcache.example.ehCache.Entity.Book;
import ehcache.example.ehCache.Service.AdminService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {


    @Mock
    private AdminDao adminDao ;

    @InjectMocks
    private AdminService adminService ;

    private Admin admin1;
    private Admin admin2;

        @BeforeEach
        void setUp() {

             admin1 = Admin.builder().name("waho").username("traw10").password("mqldsjkfml").build() ;
             admin2 = Admin.builder().name("ex").username("rja41").password("smdlfkj").build() ;



        }

    @Test
    void AdminService_getAllAdmins_returnsAllAdminsDto() {

        Mockito.when(adminDao.findAll()).thenReturn(List.of(admin1,admin2)) ;

       List <AdminDto> adminDtos =adminService.getAllAdmins() ;


       Assertions.assertThat(adminDtos).isNotNull() ;
        Assertions.assertThat(adminDtos.size()).isEqualTo(2) ;
        Assertions.assertThat(adminDtos.get(0).getName()).isEqualTo(admin1.getName()) ;
        Assertions.assertThat(adminDtos.get(0).getUsername()).isEqualTo(admin1.getUsername()) ;

        Assertions.assertThat(adminDtos.get(1).getName()).isEqualTo(admin2.getName()) ;
        Assertions.assertThat(adminDtos.get(1).getUsername()).isEqualTo(admin2.getUsername()) ;


    }

    @Test
    void AdminService_getAdminById_ReturnsAdminDto() {


        when(adminDao.findById(1l)).thenReturn(Optional.ofNullable(admin1)) ;


        AdminDto adminDto =adminService.getAdminById(1l) ;


        // System.out.println(findBook.getId()+);

        Assertions.assertThat(adminDto).isNotNull();
        Assertions.assertThat(adminDto.getUsername()).isEqualTo(admin1.getUsername());
        Assertions.assertThat(adminDto.getName()).isEqualTo(admin1.getName());



    }

    @Test
    void AdminService_getAdminByname_ReturnsAdminDto() {


        when(adminDao.findAdminByName("hh")).thenReturn(Optional.ofNullable(admin2)) ;


        AdminDto adminDto =adminService.getAdminByname("hh") ;



        Assertions.assertThat(adminDto).isNotNull();
        Assertions.assertThat(adminDto.getUsername()).isEqualTo(admin2.getUsername());
        Assertions.assertThat(adminDto.getName()).isEqualTo(admin2.getName());




    }

    @Test
    void AdminService_getAdminByUsername_ReturnsAdminDto() {

        when(adminDao.findAdminByUsername("1l")).thenReturn(Optional.ofNullable(admin1)) ;


        AdminDto adminDto =adminService.getAdminByUsername("1l") ;


        // System.out.println(findBook.getId()+);

        Assertions.assertThat(adminDto).isNotNull();
        Assertions.assertThat(adminDto.getUsername()).isEqualTo(admin1.getUsername());
        Assertions.assertThat(adminDto.getName()).isEqualTo(admin1.getName());


    }

    @Test
    void AdminService_createNewAdmin_ReturnsAdminDto() {

            when(adminDao.save(admin1)).thenReturn(admin1) ;

            AdminDto adminDto =adminService.createNewAdmin(
                    CreateAdminDto.builder()
                    .name(admin1.getName()).password(admin1.getPassword())
                    .username(admin1.getUsername())

                    .build()
            );
        Assertions.assertThat(adminDto).isNotNull();
        Assertions.assertThat(adminDto.getUsername()).isEqualTo(admin1.getUsername());
        Assertions.assertThat(adminDto.getName()).isEqualTo(admin1.getName());


    }

    @Test
    void remAdmin() {

//arrange
            doNothing().when(adminDao).deleteById(1L);
         //act
        String check=adminService.remAdmin(1L);

        Assertions.assertThat(check).isEqualTo("Admin removed");



    }

    @Test
    void AdminService_updateAdmin_returnsAdminDto   () {

            //arrange
        Admin updatedAdmin = Admin.builder()
                .id(1L)
                .name("New Name")
                .username("newUsername")
                .password("secret")
                .build();
        AdminDto updateDto = AdminDto.builder()
                .name("New Name")
                .username("newUsername")
                .build();

            when(adminDao.findById(1l)).thenReturn(Optional.ofNullable(admin1));
        when(adminDao.save(any(Admin.class))).thenReturn(updatedAdmin) ;




//act
        AdminDto result =adminService.updateAdmin(1l,updateDto) ;


        //assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUsername()).isEqualTo(updatedAdmin.getUsername());
        Assertions.assertThat(result.getName()).isEqualTo(updatedAdmin.getName());



    }



}