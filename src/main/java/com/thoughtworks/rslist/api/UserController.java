package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.Entity.UserEntity;
import com.thoughtworks.rslist.Service.UserService;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.CommonError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity addUserToRepository(@RequestBody @Valid User user){
        UserEntity newUser = User.userEntityBuilder(user);
        UserEntity userAdded = userService.addOrSaveUser(newUser);
        int userId = userAdded.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @GetMapping("/user/{index}")
    public ResponseEntity<UserEntity> searchUserFromRepository(@PathVariable int index) {
        UserEntity searchResult = userService.getUserInfoById(index);
        return ResponseEntity.ok(searchResult);
    }

    @DeleteMapping("/user/{index}")
    public ResponseEntity deleteUserFromRepository(@PathVariable int index) {
        userService.deleteUserById(index);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleException(MethodArgumentNotValidException ex) {
        CommonError commonError = new CommonError();
        commonError.setError("invalid user");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonError);
    }
}
