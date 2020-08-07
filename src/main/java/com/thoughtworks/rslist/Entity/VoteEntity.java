package com.thoughtworks.rslist.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "votes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer voteNum;
    private LocalDateTime voteTime;
    private String userId;
    private String eventId;

    @ManyToOne
    private UserEntity userEntity;
}
