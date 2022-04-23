package by.bsuir.football.service;

import by.bsuir.football.entity.Match;

public interface MatchService {

    Match getMatch(Long id);

    Match[] getAllMatches();

    //Match[] getMatchesForDate(Date date);

    Match[] getOnlineMatches();

    void save(Match match);

}