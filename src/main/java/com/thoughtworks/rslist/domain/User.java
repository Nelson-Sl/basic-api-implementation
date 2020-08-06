package com.thoughtworks.rslist.domain;

/*
Basic Input Example
{
    "eventName": "添加一条热搜",
    "keyword": "娱乐",
    "user": {
      "userName": "xiaowang",
      "age": 19,
      "gender": "female",
      "email": "a@thoughtworks.com",
      "phone": 18888888888
    }
}
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.engine.internal.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Size(max = 8)
    @NotNull
    private String userName;
    @Min(18)
    @Max(100)
    private int age;
    @NotNull
    private String gender;
    @Email
    private String email;
    @Pattern(regexp = "1\\d{10}")
    private String phone;
}
