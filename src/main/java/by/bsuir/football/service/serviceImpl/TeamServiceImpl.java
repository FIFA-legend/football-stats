package by.bsuir.football.service.serviceImpl;

import by.bsuir.football.entity.Team;
import by.bsuir.football.repository.TeamRepository;
import by.bsuir.football.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public Team getTeam(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    @Override
    public Team[] getAllTeams() {
        List<Team> teams = (List<Team>) teamRepository.findAll();
        return teams.toArray(new Team[0]);
    }

    /*@Override
    public Team[] getTeamsByCountry(String country) {
        List<Team> teams = teamRepository.findAllByCountry(country);
        return teams.toArray(new Team[0]);
    }*/

    @Override
    public void save(Team team) {
        teamRepository.save(team);
    }

}
