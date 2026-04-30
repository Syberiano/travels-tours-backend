package com.travels.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travels.backend.model.Event;
import com.travels.backend.model.EventType;
import com.travels.backend.model.Package;
import com.travels.backend.model.User;
import com.travels.backend.repository.EventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;

    public Event recordEvent(EventType type, Package travelPackage, User user, String userIp, String userAgent) {
        log.info("Registrando evento de tipo {} para paquete {}", type, travelPackage.getId());

        Event event = Event.builder()
                .type(type)
                .travelPackage(travelPackage)
                .user(user) 
                .userIp(userIp)
                .userAgent(userAgent)
                .build();

        return eventRepository.save(event);
    }

    public List<Event> getEventsByPackage(Long packageId) {
        return eventRepository.findByTravelPackageId(packageId);
    }

    public List<Event> getEventsByUser(Long userId) {
        return eventRepository.findByUserId(userId);
    }

    public Long getPackageViewCount(Long packageId) {
        return eventRepository.countViewsByPackageId(packageId);
    }

    public List<Event> getEventsByDateRange(Long packageId, LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.findEventsByPackageAndDateRange(packageId, startDate, endDate);
    }

    public Double calculateConversionRate(Long packageId) {
        Long views = eventRepository.countByTravelPackageIdAndType(packageId, EventType.CLICK);
        Long bookings = eventRepository.countByTravelPackageIdAndType(packageId, EventType.BOOKING);

        if (views == 0) {
            return 0.0;
        }

        return (double) bookings / views * 100;
    }
}
