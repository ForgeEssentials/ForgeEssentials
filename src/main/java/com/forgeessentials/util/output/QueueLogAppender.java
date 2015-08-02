package com.forgeessentials.util.output;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class QueueLogAppender extends AbstractAppender
{

    private int maxCapacity = 500;

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    public QueueLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, int maxCapacity)
    {
        super(name, filter, layout, ignoreExceptions);
        this.maxCapacity = maxCapacity;
    }

    public QueueLogAppender(String string, int maxCapacity)
    {
        this(string, null, PatternLayout.createLayout(null, null, null, null, null), false, maxCapacity);
    }

    @Override
    public void append(final LogEvent event)
    {
        while (queue.size() >= maxCapacity)
            queue.remove();
        String line = getLayout().toSerializable(event).toString();
        line = line.substring(0, line.length() - System.lineSeparator().length());
        queue.add(line);
    }

    public BlockingQueue<String> getQueue()
    {
        return queue;
    }

    public int getMaxCapacity()
    {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity)
    {
        this.maxCapacity = maxCapacity;
    }

    @PluginFactory
    public static QueueLogAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("ignoreExceptions") String ignore,
            @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filters") Filter filter, @PluginAttribute("target") String target,
            @PluginAttribute("maxCapacity") String maxCapacity)
    {
        boolean ignoreExceptions = Boolean.parseBoolean(ignore);
        int capacity = Integer.parseInt(maxCapacity);
        if (name == null)
        {
            LOGGER.error("No name provided for QueueLogAppender");
            return null;
        }
        if (target == null)
            target = name;
        if (layout == null)
            layout = PatternLayout.createLayout(null, null, null, null, null);
        return new QueueLogAppender(name, filter, layout, ignoreExceptions, capacity);
    }

}