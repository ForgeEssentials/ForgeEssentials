package com.forgeessentials.playerlogger.PersistenceProviders;

import java.util.Properties;

import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQLDialect;

import com.forgeessentials.util.output.logger.LoggingHandler;

public class PersistenceSelector {
	public static Properties getPersistenceProps(String type) {
		Properties props = new Properties();
		if(type.equals("playerlogger_h2")) {
        	props.put("hibernate.dialect", H2Dialect.class);
        	props.put("hibernate.connection.driver_class", "org.h2.Driver");
        	props.put("hibernate.connection.url", "jdbc:h2:playerlogger");
        	props.put("hibernate.connection.username", "forgeessentials");
            props.put("hibernate.connection.password", "forgeessentials");
            props.put("hibernate.hbm2ddl.auto", "update");
            props.put("hibernate.jdbc.batch_size", 30);
            props.put("hibernate.order_inserts", true);
            props.put("hibernate.order_updates", true);
            props.put("hibernate.jdbc.batch_versioned_data", true);
		}
		else if(type.equals("playerlogger_mysql")) {
        	props.put("hibernate.dialect", MySQLDialect.class);
        	props.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        	props.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/playerlogger");
        	props.put("hibernate.connection.username", "forgeessentials");
            props.put("hibernate.connection.password", "forgeessentials");
            props.put("hibernate.hbm2ddl.auto", "update");
            props.put("hibernate.jdbc.batch_size", 30);
            props.put("hibernate.order_inserts", true);
            props.put("hibernate.order_updates", true);
            props.put("hibernate.jdbc.batch_versioned_data", true);
		}
		else {
			LoggingHandler.felog.error("FAILED TO GET PLAYERLOGGER DATABASE TYPE");
			throw new RuntimeException("FAILED TO GET PLAYERLOGGER DATABASE TYPE");
		}
		return props;
	}
}
