package com.forgeessentials.playerlogger.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "action", discriminatorType = DiscriminatorType.INTEGER)  
@DiscriminatorValue(value = "0")  
public class Action {

    @Id
    @GeneratedValue
    @Column(name = "id")
    public Long id;

    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date time;

    @Column(name = "x")
    public int x;

    @Column(name = "y")
    public int y;

    @Column(name = "z")
    public int z;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "dimension")
    public WorldData world;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    public PlayerData player;
    
}
