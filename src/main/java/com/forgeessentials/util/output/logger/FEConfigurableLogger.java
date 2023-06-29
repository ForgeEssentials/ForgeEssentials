package com.forgeessentials.util.output.logger;

import org.apache.logging.log4j.Marker;

import com.forgeessentials.core.ForgeEssentials;

public class FEConfigurableLogger {
	// Fatal
	public void fatal(String message) {
		LoggingHandler.feloger.fatal(message);
	}

	public void fatal(String string, Throwable e) {
		LoggingHandler.feloger.fatal(string, e);
	}

	public void fatal(String text, Object... args) {
		LoggingHandler.feloger.fatal(text, args);
	}

	public void fatal(Marker REGISTRIES, String string, Object... args) {
		LoggingHandler.feloger.fatal(REGISTRIES, string, args);
	}

	// Error
	public void error(String message) {
		LoggingHandler.feloger.error(message);
	}

	public void error(String string, Throwable e) {
		LoggingHandler.feloger.error(string, e);
	}

	public void error(String text, Object... args) {
		LoggingHandler.feloger.error(text, args);
	}

	public void error(Marker REGISTRIES, String string, Object... args) {
		LoggingHandler.feloger.error(REGISTRIES, string, args);
	}

	// Warn
	public void warn(String message) {
		LoggingHandler.feloger.warn(message);
	}

	public void warn(String string, Throwable e) {
		LoggingHandler.feloger.warn(string, e);
	}

	public void warn(String text, Object... args) {
		LoggingHandler.feloger.warn(text, args);
	}

	public void warn(Marker REGISTRIES, String string, Object... args) {
		LoggingHandler.feloger.warn(REGISTRIES, string, args);
	}

	// Info
	public void info(String message) {
		LoggingHandler.feloger.info(message);
	}

	public void info(String string, Throwable e) {
		LoggingHandler.feloger.info(string, e);
	}

	public void info(String text, Object... args) {
		LoggingHandler.feloger.info(text, args);
	}

	public void info(Marker REGISTRIES, String string, Object... args) {
		LoggingHandler.feloger.info(REGISTRIES, string, args);
	}

	// Debug
	public void debug(String message) {
		if (ForgeEssentials.isDebug()) {
			LoggingHandler.feloger.info(message);
		} else {
			LoggingHandler.feloger.debug(message);
		}
	}

	public void debug(String string, Throwable e) {
		if (ForgeEssentials.isDebug()) {
			LoggingHandler.feloger.debug(string, e);
		} else {
			LoggingHandler.feloger.debug(string, e);
		}
	}

	public void debug(String text, Object... args) {
		if (ForgeEssentials.isDebug()) {
			LoggingHandler.feloger.debug(text, args);
		} else {
			LoggingHandler.feloger.debug(text, args);
		}
	}

	public void debug(Marker REGISTRIES, String string, Object... args) {
		if (ForgeEssentials.isDebug()) {
			LoggingHandler.feloger.debug(REGISTRIES, string, args);
		} else {
			LoggingHandler.feloger.debug(REGISTRIES, string, args);
		}
	}
}
