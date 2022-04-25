package by.bsuir.football.service;

import by.bsuir.football.entity.Event;

public interface EventService {

    void saveMatchEvent(Long matchId, Event event);

}
