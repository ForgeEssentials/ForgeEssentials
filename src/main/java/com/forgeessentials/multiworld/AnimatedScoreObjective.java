package com.forgeessentials.multiworld;

import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

public class AnimatedScoreObjective extends ScoreObjective
{
    int frame = 0;
    int size;
    int len;
    boolean alwaysAnimate;

    public AnimatedScoreObjective(Scoreboard theScoreboardIn, String nameIn,
            IScoreObjectiveCriteria objectiveCriteriaIn, int frame, int size)
    {
        this(theScoreboardIn, nameIn, objectiveCriteriaIn, frame, size, false);
    }

    public AnimatedScoreObjective(Scoreboard theScoreboardIn, String nameIn,
            IScoreObjectiveCriteria objectiveCriteriaIn, int frame, int size, boolean alwaysAnimate)
    {
        super(theScoreboardIn, nameIn, objectiveCriteriaIn);
        this.frame = frame;
        this.size = size;
        this.len = nameIn.length();
        this.alwaysAnimate = alwaysAnimate;
    }

    @Override public void setDisplayName(String nameIn)
    {
        this.len = nameIn.length();
        this.frame = 0;
        super.setDisplayName(nameIn);
    }

    public boolean canAnimate()
    {
        return alwaysAnimate || len > size;
    }

    public void incrementFrame()
    {
        frame++;
        if (frame >= len)
        {
            frame = 0;
        }
    }

    public String getDisplayName()
    {
        String displayName = super.getDisplayName();

        if (canAnimate())
        {
            int fs = frame + size;
            return displayName.substring(frame, Math.min(fs, len)) + (fs > len ? displayName.substring(0, fs - len) : "");
        }
        else
        {
            return displayName;
        }
    }
}
