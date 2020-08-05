package com.thoughtworks.rslist.domain;

public class HotEvents {
    private String eventName;
    private String keyWord;
    private User user;

    public HotEvents() {

    }

    public HotEvents(String eventName, String keyWord) {
        this.eventName = eventName;
        this.keyWord = keyWord;
    }

    public HotEvents(String eventName, String keyWord, User user) {
        this.eventName = eventName;
        this.keyWord = keyWord;
        this.user = user;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}
    
