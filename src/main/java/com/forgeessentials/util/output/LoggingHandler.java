package com.forgeessentials.util.output;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class LoggingHandler
{

    public static final PatternLayout MC_PATTERN = PatternLayout.createLayout("[%d{HH:mm:ss}] [%t/%p] [%c]: %m%n", null, null, null, null);

    public static final int MAX_LOG_LENGTH = 2000;

    // TODO: Make STDERR appear in log!
    public static final QueueLogAppender logCache = new QueueLogAppender("fe_server_log_queue", null, MC_PATTERN, false, MAX_LOG_LENGTH);

    public static Logger felog;

    static
    {
        org.apache.logging.log4j.core.Logger rootLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        logCache.start();
        rootLogger.addAppender(logCache);
    }

    public static void init()
    {
        /* do nothing */
    }

    public static List<String> getLatestLog(int count)
    {
        if (count >= logCache.getQueue().size())
            return new ArrayList<String>(logCache.getQueue());
        ArrayList<String> lines = new ArrayList<String>(count);
        for (String line : logCache.getQueue())
            lines.add(line);
        return lines;
    }

}