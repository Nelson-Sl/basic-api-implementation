package com.thoughtworks.rslist.Repository;

import com.thoughtworks.rslist.Entity.EventEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<EventEntity,Integer> {
    List<EventEntity> findAll();
    EventEntity findById(int eventId);
}
