package pt.up.hs.uaa.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.up.hs.uaa.domain.User;
import pt.up.hs.uaa.service.spec.UserSearchQueryCriteriaConsumer;
import pt.up.hs.uaa.service.util.SearchCriteria;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final Logger log = LoggerFactory.getLogger(CustomUserRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> search(List<SearchCriteria> params) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<User> query = builder.createQuery(User.class);
        final Root<User> r = query.from(User.class);

        Predicate predicate = builder.conjunction();
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, builder, r);
        params.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.where(predicate);

        return em.createQuery(query).getResultList();
    }
}
