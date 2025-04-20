package ehcache.example.ehCache.Service;

import ehcache.example.ehCache.Dao.AdminDao;
import ehcache.example.ehCache.Dto.AdminDto;
import ehcache.example.ehCache.Dto.CreateAdminDto;
import ehcache.example.ehCache.Entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


//Use DTOs inorder to make my Inputs and Outputs Safe

@Service
public class AdminService {

    @Autowired
    private AdminDao adminDao;

    public List<AdminDto> getAllAdmins() {



        Iterable<Admin> admins=adminDao.findAll();

        List<AdminDto>adminDtos=new ArrayList<>();

        admins.forEach(admin -> adminDtos.add(AdminDto.returnAdminDto(admin)));

        return adminDtos ;



    }



    @Cacheable(cacheNames = "admins",key = "#id")
    public AdminDto getAdminById(Long id) {

        Admin admin= adminDao.findById(id).orElseThrow(() -> new RuntimeException("Admin not found for id: " + id));

        AdminDto adminDto=AdminDto.returnAdminDto(admin);

         return adminDto;

    }


    public AdminDto getAdminByname(String name) {

        Admin admin= adminDao.findAdminByName(name).orElseThrow(() -> new RuntimeException("Admin not found for name: " + name));

        return AdminDto.returnAdminDto(admin);

    }
    public AdminDto getAdminByUsername(String username) {

        Admin admin= adminDao.findAdminByUsername(username).orElseThrow(() -> new RuntimeException("Admin not found for username: " + username));

        return AdminDto.returnAdminDto(admin) ;

    }

    public AdminDto createNewAdmin(CreateAdminDto createAdminDto) {


        Admin admin=Admin.returnAdmin(createAdminDto);

        adminDao.save(admin);


        return AdminDto.returnAdminDto(admin);



    }

    @CacheEvict(cacheNames = "admins",key = "#id")
    public String remAdmin(Long id) {

        adminDao.deleteById(id) ;

        return "Admin removed" ;

    }


    //this updates only name and username
    @CachePut(cacheNames = "admins",key = "#id")
    public AdminDto updateAdmin(Long id , AdminDto adminDto) {


        Admin admin= adminDao.findById(id).orElseThrow(() -> new RuntimeException("Admin not found for id: " + id));


        admin.setName(adminDto.getName());
        admin.setUsername(adminDto.getUsername());

         adminDao.save(admin);

         return AdminDto.returnAdminDto(admin) ;

    }

}
