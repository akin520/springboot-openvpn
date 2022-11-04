package com.akin.springbootopenvpn.repository;

import com.akin.springbootopenvpn.entity.LoginLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoginLogRepository extends JpaRepository<LoginLogEntity,Long> {

    @Query(value = "select * from login_log order by id desc limit 50",nativeQuery = true)
    List<LoginLogEntity> findId();

}
