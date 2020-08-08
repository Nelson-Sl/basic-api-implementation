package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.rslist.Entity.EventEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;
import javax.validation.Valid;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotEvents {
    private String eventName;
    private String keyWord;
   // @Valid
    // private User user;
    private String userId;
    private int voteNum;

   // @JsonIgnore
   // public User getUser() { return user; }

  // @JsonProperty
  //  public void setUser(User user) { this.user = user; }

    public static EventEntity eventEntityBuilder(HotEvents event) {
        EventEntity result = EventEntity.builder()
                .eventName(event.getEventName())
                .keyWord(event.getKeyWord())
                .userId(event.userId)
                .voteNum(event.getVoteNum()).build();
        return result;
    }
}
    
