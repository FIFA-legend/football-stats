package by.bsuir.football.repository.impl;

import by.bsuir.football.entity.Match;
import by.bsuir.football.repository.filtration.MatchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Transactional
public class MatchRepositoryImpl {

    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public MatchRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<Match> getAllByParameters(MatchFilter filter) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Match> matchCriteria = cb.createQuery(Match.class);
        Root<Match> matchRoot = matchCriteria.from(Match.class);

        List<Predicate> predicates = collectPredicates(cb, filter, matchRoot);
        Predicate[] array = new Predicate[predicates.size()];

        matchCriteria.select(matchRoot).where(predicates.toArray(array));
        List<Match> result = em.createQuery(matchCriteria).getResultList();

        em.getTransaction().commit();
        return result;
    }

    private List<Predicate> collectPredicates(CriteriaBuilder cb, MatchFilter filter, Root<Match> root) {
        List<Predicate> predicates = new LinkedList<>();
        predicates.add(filterBySeason(cb, filter, root));
        predicates.add(filterByLeague(cb, filter, root));
        predicates.add(filterByStage(cb, filter, root));
        predicates.add(filterByStatus(cb, filter, root));
        predicates.add(filterByDate(cb, filter, root));
        predicates.add(filterByTeam(cb, filter, root));
        return predicates.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Predicate filterBySeason(CriteriaBuilder cb, MatchFilter filter, Root<Match> root) {
        if (filter.getSeason() != null) {
            return cb.equal(root.get("season_id"), filter.getSeason().getId());
        } else {
            return null;
        }
    }

    private Predicate filterByLeague(CriteriaBuilder cb, MatchFilter filter, Root<Match> root) {
        if (filter.getLeague() != null) {
            return cb.equal(root.get("league_id"), filter.getLeague().getId());
        } else {
            return null;
        }
    }

    private Predicate filterByStage(CriteriaBuilder cb, MatchFilter filter, Root<Match> root) {
        if (filter.getStage() != null) {
            return cb.equal(root.get("stage_id"), filter.getStage().getId());
        } else {
            return null;
        }
    }

    private Predicate filterByStatus(CriteriaBuilder cb, MatchFilter filter, Root<Match> root) {
        if (filter.getStatus() != null) {
            return cb.equal(root.get("status"), filter.getStatus().toString());
        } else {
            return null;
        }
    }

    private Predicate filterByDate(CriteriaBuilder cb, MatchFilter filter, Root<Match> root) {
        if (filter.getStartPeriod() != null && filter.getEndPeriod() != null) {
            return cb.between(root.get("start_date"), filter.getStartPeriod(), filter.getEndPeriod());
        } else if (filter.getStartPeriod() != null) {
            return cb.greaterThanOrEqualTo(root.get("start_date"), filter.getStartPeriod());
        } else if (filter.getEndPeriod() != null) {
            return cb.lessThanOrEqualTo(root.get("start_date"), filter.getEndPeriod());
        } else {
            return null;
        }
    }

    private Predicate filterByTeam(CriteriaBuilder cb, MatchFilter filter, Root<Match> root) {
        if (filter.getTeam1() != null && filter.getTeam2() != null) {
            return cb.or(cb.and(cb.equal(root.get("home_team_id"), filter.getTeam1().getId()), cb.equal(root.get("away_team_id"), filter.getTeam2().getId())),
                    cb.and(cb.equal(root.get("home_team_id"), filter.getTeam2().getId()), cb.equal(root.get("away_team_id"), filter.getTeam1().getId())));
        } else if (filter.getTeam1() != null) {
            return cb.or(cb.equal(root.get("home_team_id"), filter.getTeam1().getId()), cb.equal(root.get("away_team_id"), filter.getTeam1().getId()));
        } else if (filter.getTeam2() != null) {
            return cb.or(cb.equal(root.get("home_team_id"), filter.getTeam2().getId()), cb.equal(root.get("away_team_id"), filter.getTeam2().getId()));
        } else {
            return null;
        }
    }

}
