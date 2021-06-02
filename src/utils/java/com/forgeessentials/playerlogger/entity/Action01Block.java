package com.forgeessentials.playerlogger.entity;

import java.sql.Blob;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 */
@Entity
@DiscriminatorValue(value = "1")
public class Action01Block extends Action
{

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    public ActionBlockType type;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "block_id")
    public BlockData block;

    @Column(name = "metadata")
    public int metadata;

    @Column(name = "entity")
    public Blob entity;

    public static enum ActionBlockType
    {
        PLACE, BREAK, DETONATE, USE_LEFT, USE_RIGHT, BURN
    }

}
