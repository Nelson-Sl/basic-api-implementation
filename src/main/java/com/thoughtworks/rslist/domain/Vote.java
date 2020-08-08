package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.thoughtworks.rslist.Entity.EventEntity;
import com.thoughtworks.rslist.Entity.VoteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    private int voteNum;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime voteTime;
    private String userId;
    private String eventId;

    public static VoteEntity voteEntityBuilder(Vote vote) {
        VoteEntity result = VoteEntity.builder()
                .voteNum(vote.getVoteNum())
                .voteTime(vote.getVoteTime())
                .userId(vote.getUserId())
                .eventId(vote.getEventId()).build();
        return result;
    }
}
