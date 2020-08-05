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

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class User {
    @Size(max = 8)
    @NotNull
    private String userName;
    @Min(18)
    private int age;
    @NotNull
    private String gender;
    private String email;
    private String phone;

    public User(String userName, int age, String gender, String email, String phone) {
        this.userName = userName;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
