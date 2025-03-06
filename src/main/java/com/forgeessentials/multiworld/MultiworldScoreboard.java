package com.forgeessentials.multiworld;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;

import com.google.common.collect.Lists;

public class MultiworldScoreboard extends ServerScoreboard
{

    public Multiworld multiworld;

    public List<AnimatedScoreObjective> animatedScoreObjectives = new ArrayList<>();
    public MultiworldScoreboard(MinecraftServer mcServer, Multiworld multiworld)
    {
        super(mcServer);
        this.multiworld = multiworld;
    }

    @Override public ScoreObjective addScoreObjective(String name, IScoreObjectiveCriteria criteria)
    {
        ScoreObjective scoreobjective = this.getObjective(name);

        if (scoreobjective != null)
        {
            throw new IllegalArgumentException("An objective with the name \'" + name + "\' already exists!");
        }
        else
        {
            scoreobjective = new AnimatedScoreObjective(this, name, criteria, 0, 16);
            animatedScoreObjectives.add((AnimatedScoreObjective) scoreobjective);
            List<ScoreObjective> list = (List) this.scoreObjectiveCriterias.get(criteria);

            if (list == null)
            {
                list = Lists.<ScoreObjective>newArrayList();
                this.scoreObjectiveCriterias.put(criteria, list);
            }

            list.add(scoreobjective);
            this.scoreObjectives.put(name, scoreobjective);
            this.onScoreObjectiveAdded(scoreobjective);
            return scoreobjective;
        }
    }

    @Override public void removeObjective(ScoreObjective p_96519_1_)
    {
        if (p_96519_1_ instanceof AnimatedScoreObjective)
        {
            animatedScoreObjectives.remove(p_96519_1_);
        }
        super.removeObjective(p_96519_1_);
    }

    public void incrementFrameAndUpdate()
    {
        for (AnimatedScoreObjective objective : animatedScoreObjectives)
        {
            if (objective.canAnimate())
            {
                objective.incrementFrame();
                multiworld.sendPacketToAllPlayers(new S3BPacketScoreboardObjective(objective, 2));
            }
        }
    }
}

