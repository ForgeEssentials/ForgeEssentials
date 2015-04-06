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
public class PlayerData {

    @Id
    @GeneratedValue
    @Column(name = "id")
    public Long id;

    @Column(name = "uuid", length = 36)
    public String uuid;
    
    @StaticMetamodel(PlayerData.class)
    public static class Meta {
      public static volatile SingularAttribute<PlayerData, Long> id;
      public static volatile SingularAttribute<PlayerData, String> uuid;
    }

}
