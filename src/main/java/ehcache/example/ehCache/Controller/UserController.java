package ehcache.example.ehCache.Controller;

import ehcache.example.ehCache.Dto.UserDto;
import ehcache.example.ehCache.Dto.CreateUserDto;
import ehcache.example.ehCache.Service.UserService;
import ehcache.example.ehCache.auth.Dto.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/C2/user")

@RestController
public class UserController {


    @Autowired
    private UserService userService;



    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> userDtos= userService.getAllUsers();
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    /*
    @PostConstruct
    public void init(){

        adminService.InitAdmin();

    }


*/

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") long id) {
        UserDto adminDto = userService.getUserById(id);
        return new ResponseEntity<>(adminDto, HttpStatus.OK);
    }

   @PostMapping("/createNewUser")
    public ResponseEntity<UserDto> createNewUser(@RequestBody CreateUserDto createUserDto) {
        return new ResponseEntity<>(userService.createNewUser(createUserDto), HttpStatus.CREATED);
    }

    @GetMapping("/getUserByName/{name}")
    public ResponseEntity<UserDto> getUserByName(@PathVariable("name") String name) {
        UserDto userDto = userService.getUserByname(name);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }


    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username) {
        UserDto userDto = userService.getUserByUsername(username);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<?>changePaassword(
            @RequestBody ChangePasswordRequest request
            ,
            Principal connectedUser
    )
    {



        userService.changePassword(request,connectedUser) ;
        return  ResponseEntity.ok().build() ;

    }

    @DeleteMapping("/remUser/{id}")
    public ResponseEntity<String> remUser(@PathVariable("id") long id) {
        userService.remUser(id);
        return ResponseEntity.ok("user removed");
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserDto> update_user(@RequestBody UserDto userDto) {

        return new ResponseEntity<>(userService.updateUser(userDto), HttpStatus.OK);

    }




}



