package pt.up.hs.uaa.repository;

import pt.up.hs.uaa.domain.User;
import pt.up.hs.uaa.service.util.SearchCriteria;

import java.util.List;

public interface CustomUserRepository {

    List<User> search(List<SearchCriteria> params);
}
