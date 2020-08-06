package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.Entity.UserEntity;
import com.thoughtworks.rslist.Repository.UserRepository;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.CommonError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class UserController {
    private static List<User> userList = Stream.of(
            new User("Tony",28,"Male", "tony@sina.cn","17458957454"),
            new User("Mark",25,"Male", "mark@sina.cn","17458957455"),
            new User("Jenny",27,"Female", "jenny@sina.cn","17458957456"))
            .collect(Collectors.toList());

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static List<User> getUserList() {
        return userList;
    }

    @PostMapping("/user")
    public ResponseEntity addUser(@RequestBody @Valid User user) {
        for(User existingUser: userList) {
            if(existingUser.getUserName().equals(user.getUserName())) {
                return ResponseEntity.ok().build();
            }
        }
        userList.add(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(userList.size()-1));
    }

    @PostMapping("/addUser")
    public ResponseEntity addUserToRepository(@RequestBody @Valid User user){
        UserEntity newUser = UserEntity.builder()
                .userName(user.getUserName())
                .age(user.getAge())
                .gender(user.getGender())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();

        userRepository.save(newUser);
        return ResponseEntity.created(null).build();
    }

    @GetMapping("/user/{index}")
    public ResponseEntity<UserEntity> searchUserFromRepository(@PathVariable int index) {
        UserEntity searchResult = userRepository.findById(index);
        return ResponseEntity.ok(searchResult);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleException(MethodArgumentNotValidException ex) {
        CommonError commonError = new CommonError();
        commonError.setError("invalid user");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonError);
    }
}
