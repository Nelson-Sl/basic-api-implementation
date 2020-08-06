package com.thoughtworks.rslist.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "name")
    private String userName;
    private Integer age;
    private String gender;
    private String email;
    private String phone;
    private Integer vote;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "userId")
    private List<EventEntity> eventEntityList;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "userId")
    private List<VoteEntity> voteEntityList;
}
