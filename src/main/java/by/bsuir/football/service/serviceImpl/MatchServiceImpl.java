package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.entity.Match;
import by.bsuir.football.repository.MatchRepository;
import by.bsuir.football.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Match getMatch(Long id) {
        return matchRepository.findById(id).orElse(null);
    }

    @Override
    public Match[] getAllMatches() {
        List<Match> matches = (List<Match>) matchRepository.findAll();
        return matches.toArray(new Match[0]);
    }

    /*@Override
    public Match[] getMatchesForDate(Date date) {
        List<Match> matchList = matchRepository.getAllByStartDateOrderByLeague(date);
        Date now = new Date(Calendar.getInstance().getTimeInMillis());
        if (date.after(now)) {
            for (Match match : matchList) {
                match.setEvents(null);
            }
        } else if (date.toString().equals(now.toString())) {
            Time time = new Time(now.getTime());
            for (Match match : matchList) {
                if (match.getStartTime() == null || match.getStartTime().toString().compareTo(time.toString()) > 0) {
                    match.setEvents(null);
                }
            }
        }
        return matchList.toArray(new Match[0]);
    }*/

    @Override
    public Match[] getOnlineMatches() {
        int twoHours = 2 * 60 * 60 * 1000;
        int threeHours = 3 * 60 * 60 * 1000;
        long now = new java.util.Date().getTime();
        List<Match> onlineMatches = new LinkedList<>();
        List<Match> matches = (List<Match>) matchRepository.findAll();
        for (Match match : matches) {
            long date = match.getStartDate().toEpochDay();
            long time = match.getStartTime().toNanoOfDay() + threeHours;
            long matchTime = date + time;
            if (matchTime < now && matchTime > now - twoHours) {
                onlineMatches.add(match);
            }
        }
        return onlineMatches.toArray(new Match[0]);
    }

    @Override
    public void save(Match match) {
        matchRepository.save(match);
    }

}