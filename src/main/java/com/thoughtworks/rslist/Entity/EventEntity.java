package com.thoughtworks.rslist.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = "userId")
public class EventEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String eventName;
    private String keyWord;
    // private String userId;
    private Integer voteNum;

    @JsonIgnore
    @ManyToOne
    private UserEntity user;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "event")
    private List<VoteEntity> voteEntityList;
}
