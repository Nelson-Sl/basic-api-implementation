package com.thoughtworks.rslist.Repository;

import com.thoughtworks.rslist.Entity.VoteEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VoteRepository extends CrudRepository<VoteEntity,Integer> {
    List<VoteEntity> findAll();
    List<VoteEntity> findAllByVoteTimeBetween(LocalDateTime start, LocalDateTime end);
    @Query("select v from VoteEntity v where v.user.id=:userId and v.event.id=:eventId")
    List<VoteEntity> findAllByUserIdAndEventId(@Param("userId") int userId, @Param("eventId") int eventId);
    @Query("select v from VoteEntity v where v.user.id=:userId and v.event.id=:eventId")
    List<VoteEntity> findAllByUserIdAndEventId(@Param("userId") int userId,
                                               @Param("eventId") int eventId, Pageable pageable);

    /*
    @Query例子 (使用正常SQL可设置native_query = true)
    @Query(value = SELECT v FROM votes v WHERE v.user_id = :userId AND v.event_id = :eventId)
    List<VoteEntity> findAllByUserIdAndEventId(String userId, String eventId);
     */
}
