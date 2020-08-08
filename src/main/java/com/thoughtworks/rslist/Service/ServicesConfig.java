package com.thoughtworks.rslist.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ServicesConfig {
    @Bean
    public RsService rsService() {
        return new RsService();
    }

    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public VoteService voteService() {
        return new VoteService();
    }
}
