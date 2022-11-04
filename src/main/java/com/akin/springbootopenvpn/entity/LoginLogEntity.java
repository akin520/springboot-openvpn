package com.akin.springbootopenvpn.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;


@Data
@Entity
@Table(name = "login_log")
public class LoginLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //ID自增
    private Long id;
    private String username;
    private Timestamp login_time;
    private String trusted_ip;
    private String trusted_port;
    private String protocol;
    private String remote_ip;
    private Timestamp end_time;
    private String bytes_received;
    private String bytes_sent;
}
