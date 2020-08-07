package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.Entity.UserEntity;
import com.thoughtworks.rslist.Repository.UserRepository;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.CommonError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/addUser")
    public ResponseEntity addUserToRepository(@RequestBody @Valid User user){
        UserEntity newUser = UserEntity.builder()
                .userName(user.getUserName())
                .age(user.getAge())
                .gender(user.getGender())
                .email(user.getEmail())
                .phone(user.getPhone())
                .vote(user.getVote())
                .build();

        UserEntity userInput = userRepository.save(newUser);
        int userId = userInput.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @GetMapping("/user/{index}")
    public ResponseEntity<UserEntity> searchUserFromRepository(@PathVariable int index) {
        UserEntity searchResult = userRepository.findById(index);
        return ResponseEntity.ok(searchResult);
    }

    @DeleteMapping("/deleteUser/{index}")
    public ResponseEntity deleteUserFromRepository(@PathVariable int index) {
        userRepository.deleteById(index);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleException(MethodArgumentNotValidException ex) {
        CommonError commonError = new CommonError();
        commonError.setError("invalid user");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonError);
    }
}
