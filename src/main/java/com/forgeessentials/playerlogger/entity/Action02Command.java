package com.forgeessentials.playerlogger.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 */
@Entity
@DiscriminatorValue(value = "2")
public class Action02Command extends Action {

	@Column(name = "command")
	public String command;

	@Column(name = "arguments")
	public String arguments;

}
