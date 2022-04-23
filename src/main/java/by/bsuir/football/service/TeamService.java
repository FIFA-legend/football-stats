package by.bsuir.football.service;

import by.bsuir.football.entity.Team;

public interface TeamService {

    Team getTeam(Long id);

    Team[] getAllTeams();

    //Team[] getTeamsByCountry(String country);

    void save(Team team);

}
