package com.akin.springbootopenvpn.repository;

import com.akin.springbootopenvpn.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findByIdAndName(Long id,String name);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update user set expired_time=date_add(now(),INTERVAL 1 MONTH),active=1 where id=?1",nativeQuery = true)
    void addTimeById(Long uid);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update user set expired_time=date_add(now(),INTERVAL 1 MONTH),active=1 where active=1",nativeQuery = true)
    void updateAll();
}
