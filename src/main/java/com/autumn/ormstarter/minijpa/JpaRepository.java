package com.autumn.ormstarter.minijpa;

import java.util.List;
import java.util.Optional;

/**
 * @author ziyuan
 * @since 2024.09
 */
public interface JpaRepository<T, ID> {

    <S extends T> S save(S entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);
}
