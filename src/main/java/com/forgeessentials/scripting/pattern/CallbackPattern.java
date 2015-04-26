package com.forgeessentials.scripting.pattern;

import java.util.List;

public class CallbackPattern extends Pattern
{

    public interface PatternCallback
    {
        public void call(List<String> arguments);
    }

    private PatternCallback callback;

    public CallbackPattern(String pattern, PatternCallback callback)
    {
        super(pattern);
        this.callback = callback;
    }

    public void setCallback(PatternCallback callback)
    {
        this.callback = callback;
    }

    public PatternCallback getCallback()
    {
        return callback;
    }

    @Override
    public void onMatch(List<String> arguments)
    {
        callback.call(arguments);
    }

}