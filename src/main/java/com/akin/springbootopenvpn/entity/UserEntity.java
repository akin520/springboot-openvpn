package com.akin.springbootopenvpn.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames= {"name"}))
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //ID自增
    private Long id;
    private String name;
    private String password;
    private Integer active;
    private Timestamp expired_time;
}

