package org.example.mapper;

import com.autumn.ormstarter.minijpa.JpaRepository;
import org.example.Bean.User;

import java.util.List;
import java.util.Optional;

/**
 * @author ziyuan
 * @since 2024.09
 */
public interface MiniJpaRepository extends JpaRepository<User,Integer> {
    Optional<User> getOneById(Integer id);
}
