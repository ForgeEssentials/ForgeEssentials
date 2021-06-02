package com.forgeessentials.playerlogger.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table
public class WorldData
{

    @Id
    @Column(name = "id")
    public Integer id;

    @Column(name = "name")
    public String name;

}
