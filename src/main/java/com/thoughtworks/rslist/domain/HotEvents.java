package com.thoughtworks.rslist.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;
import javax.validation.Valid;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotEvents {
    private String eventName;
    private String keyWord;
   // @Valid
    // private User user;
    private String userId;

   // @JsonIgnore
   // public User getUser() { return user; }

  // @JsonProperty
  //  public void setUser(User user) { this.user = user; }
}
    
