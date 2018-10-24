package com.forgeessentials.util.output;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.forgeessentials.core.ForgeEssentials;

public class LoggingHandler
{

    public static final PatternLayout MC_PATTERN = PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss}] [%t/%level] [%logger]: %msg%n").build();

    public static final int MAX_LOG_LENGTH = 2000;

    // TODO: Make STDERR appear in log!
    public static final QueueLogAppender logCache = new QueueLogAppender("fe_server_log_queue", null, MC_PATTERN, true, MAX_LOG_LENGTH);

    public static final org.apache.logging.log4j.Logger felog = LogManager.getLogger(ForgeEssentials.MODID);

    static
    {
        addAppenderToAllConfigurations(logCache);
        logCache.start();
    }

    public static void addAppenderToAllConfigurations(Appender appender)
    {
        for (LoggerContext context : ((Log4jContextFactory) LogManager.getFactory()).getSelector().getLoggerContexts())
        {
            Configuration rootConfig = context.getConfiguration();
            rootConfig.addAppender(appender);
            for (LoggerConfig loggerConfig : rootConfig.getLoggers().values())
                loggerConfig.addAppender(appender, null, null);
            context.updateLoggers();
        }
    }

    public static void addAppenderToConfiguration(Appender appender, String configName)
    {
        for (LoggerContext context : ((Log4jContextFactory) LogManager.getFactory()).getSelector().getLoggerContexts())
        {
            Configuration rootConfig = context.getConfiguration();
            if (rootConfig.getName().equals(configName))
            {
                rootConfig.addAppender(appender);
                for (LoggerConfig loggerConfig : rootConfig.getLoggers().values())
                    loggerConfig.addAppender(appender, null, null);
                context.updateLoggers();
            }
        }
    }

    public static void init()
    {
        /* do nothing */
    }

    public static List<String> getLatestLog(int count)
    {
        if (count >= logCache.getQueue().size())
            return new ArrayList<String>(logCache.getQueue());

        Iterator<String> iterator = logCache.getQueue().iterator();
        for (int skip = logCache.getQueue().size() - count; skip > 0; skip--)
            iterator.next();

        ArrayList<String> lines = new ArrayList<String>(count);
        for (; iterator.hasNext() && count > 0; count--)
            lines.add(iterator.next());
        return lines;
    }

}