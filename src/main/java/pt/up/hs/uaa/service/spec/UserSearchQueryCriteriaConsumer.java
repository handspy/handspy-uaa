package pt.up.hs.uaa.service.spec;

import pt.up.hs.uaa.domain.User;
import pt.up.hs.uaa.service.util.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.Consumer;

public class UserSearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {

    private Predicate predicate;
    private final CriteriaBuilder builder;
    private final Root<User> r;

    public UserSearchQueryCriteriaConsumer(Predicate predicate, CriteriaBuilder builder, Root<User> r) {
        super();
        this.predicate = predicate;
        this.builder = builder;
        this.r = r;
    }

    @Override
    public void accept(SearchCriteria param) {

        if (param.getOperation().equalsIgnoreCase(">")) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(r.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase("<")) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(r.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase(":")) {
            if (r.get(param.getKey()).getJavaType() == String.class) {
                predicate = builder.and(predicate, builder.like(r.get(param.getKey()), "%" + param.getValue() + "%"));
            } else {
                predicate = builder.and(predicate, builder.equal(r.get(param.getKey()), param.getValue()));
            }
        }
    }

    public Predicate getPredicate() {
        return predicate;
    }
}
