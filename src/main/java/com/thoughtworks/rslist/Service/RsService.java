package com.thoughtworks.rslist.Service;

import com.thoughtworks.rslist.Entity.EventEntity;
import com.thoughtworks.rslist.Repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RsService {
    //final使用构造器注入IoC容器
    private final EventRepository eventRepository;

    public RsService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public int getEventCount() {
        return (int) eventRepository.count();
    }

    public boolean isEventExists(int eventId) {
        return eventRepository.existsById(eventId);
    }

    public EventEntity getEventById(int eventId) {
        return eventRepository.findById(eventId);
    }

    public List<EventEntity> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<EventEntity> getEventWithinRanges(int start, int end) {
        return eventRepository.findAll().subList(start,end);
    }

    public EventEntity addOrSaveEvent(EventEntity event) {
        return eventRepository.save(event);
    }

    public void deleteEventById(int eventId) {
        eventRepository.deleteById(eventId);
    }
}
