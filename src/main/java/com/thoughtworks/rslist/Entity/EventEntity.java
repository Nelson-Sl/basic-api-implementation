package com.thoughtworks.rslist.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private String eventName;
    private String keyWord;
    private String userId;

    @ManyToOne
    private UserEntity userEntity;
}
