package com.thoughtworks.rslist.Service;

import com.thoughtworks.rslist.Entity.VoteEntity;
import com.thoughtworks.rslist.Repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;

// @Service
public class VoteService {
    @Autowired
    private VoteRepository voteRepository;

    public VoteEntity addOrChangeVoteRecord(VoteEntity newVote) {
        return voteRepository.save(newVote);
    }

    public List<VoteEntity> findVoteRecordWithinTime(LocalDateTime startDate, LocalDateTime endDate) {
        return voteRepository.findAllByVoteTimeBetween(startDate,endDate);
    }

    public List<VoteEntity> findVoteRecordsByUserIdAndEventId(String userId, String eventId) {
        return voteRepository.findAllByUserIdAndEventId(userId, eventId);
    }

    public List<VoteEntity> findVoteRecordsByUserIdAndEventId(String userId, String eventId,
            Pageable pageable) {
        return voteRepository.findAllByUserIdAndEventId(userId, eventId, pageable);
    }
}
