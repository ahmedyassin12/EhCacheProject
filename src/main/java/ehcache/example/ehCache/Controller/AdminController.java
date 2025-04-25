package ehcache.example.ehCache.Controller;

import ehcache.example.ehCache.Dto.AdminDto;
import ehcache.example.ehCache.Dto.CreateAdminDto;
import ehcache.example.ehCache.Entity.Admin;
import ehcache.example.ehCache.Service.AdminService;
import ehcache.example.ehCache.auth.ChangePasswordRequest;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/C2/Admin")

@RestController
public class AdminController {


    @Autowired
    private AdminService adminService ;



    @GetMapping("/getAllAdmins")
    public ResponseEntity<List<AdminDto>> getAllAdmins() {
        List<AdminDto> adminDtos=adminService.getAllAdmins();
        return new ResponseEntity<>(adminDtos, HttpStatus.OK);
    }

    @PostConstruct
    public void init(){

        adminService.InitAdmin();



    }


    @GetMapping("/getAdminById/{id}")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable("id") long id) {
        AdminDto adminDto = adminService.getAdminById(id);
        return new ResponseEntity<>(adminDto, HttpStatus.OK);
    }

   @PostMapping("/createNewAdmin")
    public ResponseEntity<AdminDto> createNewAdmin(@RequestBody @Valid CreateAdminDto createAdminDto) {
        return new ResponseEntity<>(adminService.createNewAdmin(createAdminDto), HttpStatus.CREATED);
    }

    @GetMapping("/getAdminByName/{name}")
    public ResponseEntity<AdminDto> getManagerByName(@PathVariable("name") String name) {
        AdminDto adminDto = adminService.getAdminByname(name);
        return new ResponseEntity<>(adminDto, HttpStatus.OK);
    }


    @GetMapping("/getAdminByUsername/{username}")
    public ResponseEntity<AdminDto> getAdminByUsername(@PathVariable("username") String username) {
        AdminDto adminDto = adminService.getAdminByUsername(username);
        return new ResponseEntity<>(adminDto, HttpStatus.OK);
    }

    @DeleteMapping("/remAdmin/{id}")
    public ResponseEntity<String> remAdmin(@PathVariable("id") long id) {
        adminService.remAdmin(id);
        return ResponseEntity.ok("admin removed");
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<AdminDto> update_admin(@PathVariable Long id,@RequestBody @Valid AdminDto adminDto) {

        return new ResponseEntity<>(adminService.updateAdmin(id,adminDto), HttpStatus.OK);
    }


    @PatchMapping("/changePassword")
    public ResponseEntity<?>changePaassword(
            @RequestBody @Valid ChangePasswordRequest request
            ,
            Principal connectedUser
    )
    {



        adminService.changePassword(request,connectedUser) ;
        return  ResponseEntity.ok().build() ;

    }



}
