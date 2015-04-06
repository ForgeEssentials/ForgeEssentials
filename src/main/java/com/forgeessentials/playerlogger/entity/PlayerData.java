package com.forgeessentials.playerlogger.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table
public class PlayerData {

    @Id
    @GeneratedValue
    @Column(name = "id")
    public Long id;

    @Column(name = "uuid", length = 36)
    public String uuid;

}
