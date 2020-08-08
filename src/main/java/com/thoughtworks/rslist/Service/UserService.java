package com.thoughtworks.rslist.Service;

import com.thoughtworks.rslist.Entity.UserEntity;
import com.thoughtworks.rslist.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository =  userRepository;
    }

    public boolean isUserExists(int userId) {
        return userRepository.existsById(userId);
    }

    public UserEntity getUserInfoById(int userId) {
        return userRepository.findById(userId);
    }

    public UserEntity addOrSaveUser(UserEntity user) {
        return userRepository.save(user);
    }

    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }
}
