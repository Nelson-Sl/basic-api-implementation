package com.thoughtworks.rslist.Repository;

import com.thoughtworks.rslist.Entity.VoteEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VoteRepository extends CrudRepository<VoteEntity,Integer> {
    List<VoteEntity> findAll();
    List<VoteEntity> findAllByVoteTimeBetween(LocalDateTime start, LocalDateTime end);
    List<VoteEntity> findAllByUserIdAndEventId(String userId, String eventId);
    List<VoteEntity> findAllByUserIdAndEventId(String userId, String eventId,Pageable pageable);

    /*
    @Query例子 (使用正常SQL可设置native_query = true)
    @Query(value = SELECT v FROM votes v WHERE v.user_id = :userId AND v.event_id = :eventId)
    List<VoteEntity> findAllByUserIdAndEventId(String userId, String eventId);
     */
}
