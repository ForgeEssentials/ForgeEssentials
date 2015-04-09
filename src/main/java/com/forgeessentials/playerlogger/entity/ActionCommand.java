package com.forgeessentials.playerlogger.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 */
@Entity
@DiscriminatorValue(value = "1")  
public class ActionCommand extends Action {

    @Column(name = "command")
    public String command;
    
}
