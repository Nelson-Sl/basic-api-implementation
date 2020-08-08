package com.thoughtworks.rslist.Service;

import com.thoughtworks.rslist.Entity.EventEntity;
import com.thoughtworks.rslist.Repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RsService {
    private final EventRepository eventRepository;

    public RsService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

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
