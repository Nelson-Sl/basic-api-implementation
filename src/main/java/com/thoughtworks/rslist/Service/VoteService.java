package com.thoughtworks.rslist.Service;

import com.thoughtworks.rslist.Entity.VoteEntity;
import com.thoughtworks.rslist.Repository.VoteRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoteService {
    private VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

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
