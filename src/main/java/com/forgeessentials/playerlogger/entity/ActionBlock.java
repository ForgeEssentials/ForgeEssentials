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
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 */
@Entity
@DiscriminatorValue(value = "1")  
public class ActionBlock extends Action {

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
    
    public static enum ActionBlockType {
        PLACE, BREAK, USE_LEFT, USE_RIGHT
    }
        
    @StaticMetamodel(ActionBlock.class)
    public static class Meta extends Action.Meta {
      public static volatile SingularAttribute<ActionBlock, Long> id;
      public static volatile SingularAttribute<ActionBlock, ActionBlockType> type;
      public static volatile SingularAttribute<ActionBlock, BlockData> block;
      public static volatile SingularAttribute<ActionBlock, Integer> metadata;
      public static volatile SingularAttribute<ActionBlock, Blob> entity;
    }
    
}
