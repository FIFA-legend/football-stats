package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.entity.Event;
import by.bsuir.football.entity.Match;
import by.bsuir.football.repository.EventRepository;
import by.bsuir.football.repository.MatchRepository;
import by.bsuir.football.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {

    private final MatchRepository matchRepository;

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(MatchRepository matchRepository, EventRepository eventRepository) {
        this.matchRepository = matchRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public void saveMatchEvent(Long matchId, Event event) {
        Match match = matchRepository.findById(matchId).orElse(null);
        if (match != null) {
            eventRepository.save(event);
            match.getEvents().add(event);
            matchRepository.save(match);
        }
    }
}
