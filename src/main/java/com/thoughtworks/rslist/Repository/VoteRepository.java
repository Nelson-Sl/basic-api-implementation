package com.thoughtworks.rslist.Repository;

import com.thoughtworks.rslist.Entity.VoteEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VoteRepository extends CrudRepository<VoteEntity,Integer> {
    List<VoteEntity> findAll();
    List<VoteEntity> findByVoteTimeBetween(LocalDateTime start, LocalDateTime end);
    VoteEntity findByIdAndUserId(int voteId, String userId);
}
