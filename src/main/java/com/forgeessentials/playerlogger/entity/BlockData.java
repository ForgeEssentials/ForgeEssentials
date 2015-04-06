package com.forgeessentials.playerlogger.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 */
@Entity
@Table
public class BlockData {

    @Id
    @GeneratedValue
    @Column(name = "id")
    public Long id;

    @Column(name = "name")
    public String name;
    
    @StaticMetamodel(BlockData.class)
    public static class Meta {
      public static volatile SingularAttribute<BlockData, Long> id;
      public static volatile SingularAttribute<BlockData, String> name;
    }

}
