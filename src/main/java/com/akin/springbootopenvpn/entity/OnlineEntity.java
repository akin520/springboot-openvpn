package com.akin.springbootopenvpn.entity;

import lombok.Data;

@Data
public class OnlineEntity {
    private String username;
    private String ip;
    private String received;
    private String sent;
    private String date;
}
