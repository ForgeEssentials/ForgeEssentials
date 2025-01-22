package com.forgeessentials.playerlogger.entity;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.minecraft.util.BlockPos;
/**
 *
 */
@Entity
@Table(indexes = { //
        @Index(columnList = "player_id", name = "player_idx"), //
        @Index(columnList = "dimension", name = "world_idx"), //
        @Index(columnList = "x", name = "x_idx"), //
        @Index(columnList = "y", name = "y_idx"), //
        @Index(columnList = "z", name = "z_idx"), //
})
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverride(name = "action", column = @Column(name = "action", nullable = false, length = 8, insertable = false, updatable = false) )
@DiscriminatorColumn(name = "action", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue(value = "0")
public abstract class Action
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "action")
    public Integer action;

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

    @Transient
    private BlockPos blockPos;

    public BlockPos getBlockPos()
    {
        if (blockPos == null)
            blockPos = new BlockPos(x, y, z);
        return blockPos;
    }
}
