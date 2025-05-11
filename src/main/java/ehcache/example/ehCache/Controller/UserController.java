package ehcache.example.ehCache.Controller;

import ehcache.example.ehCache.Dto.UserDto;
import ehcache.example.ehCache.Dto.CreateUserDto;
import ehcache.example.ehCache.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/C2/user")

@RestController
public class UserController {


    @Autowired
    private UserService adminService ;



    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllAdmins() {
        List<UserDto> adminDtos=adminService.getAllUsers();
        return new ResponseEntity<>(adminDtos, HttpStatus.OK);
    }

    /*
    @PostConstruct
    public void init(){

        adminService.InitAdmin();



    }
*/

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<UserDto> getAdminById(@PathVariable("id") long id) {
        UserDto adminDto = adminService.getUserById(id);
        return new ResponseEntity<>(adminDto, HttpStatus.OK);
    }

   @PostMapping("/createNewUser")
    public ResponseEntity<UserDto> createNewAdmin(@RequestBody CreateUserDto createAdminDto) {
        return new ResponseEntity<>(adminService.createNewUser(createAdminDto), HttpStatus.CREATED);
    }

    @GetMapping("/getUserByName/{name}")
    public ResponseEntity<UserDto> getManagerByName(@PathVariable("name") String name) {
        UserDto adminDto = adminService.getUserByname(name);
        return new ResponseEntity<>(adminDto, HttpStatus.OK);
    }


    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<UserDto> getAdminByUsername(@PathVariable("username") String username) {
        UserDto adminDto = adminService.getUserByUsername(username);
        return new ResponseEntity<>(adminDto, HttpStatus.OK);
    }

    @DeleteMapping("/remUser/{id}")
    public ResponseEntity<String> remAdmin(@PathVariable("id") long id) {
        adminService.remUser(id);
        return ResponseEntity.ok("user removed");
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserDto> update_admin(@PathVariable Long id, @RequestBody UserDto userDto) {

        return new ResponseEntity<>(adminService.updateUser(id,userDto), HttpStatus.OK);
    }




}
