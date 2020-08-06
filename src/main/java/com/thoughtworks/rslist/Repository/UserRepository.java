package com.thoughtworks.rslist.Repository;

import com.thoughtworks.rslist.Entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    List<UserEntity> findAll();
    UserEntity findById(int index);
}
