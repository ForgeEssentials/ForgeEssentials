package com.forgeessentials.multiworld;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardSaveData;

public class AnimatedScoreboardSaveData extends ScoreboardSaveData
{
    private Scoreboard theScoreboard;

    public AnimatedScoreboardSaveData()
    {
        this("scoreboard");
    }

    public AnimatedScoreboardSaveData(String name)
    {
        super(name);
    }

    public void setScoreboard(Scoreboard scoreboardIn)
    {
        theScoreboard = scoreboardIn;
        super.setScoreboard(scoreboardIn);
    }

    @Override protected void readObjectives(NBTTagList nbt)
    {
        for (int i = 0; i < nbt.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbt.getCompoundTagAt(i);
            IScoreObjectiveCriteria iscoreobjectivecriteria = (IScoreObjectiveCriteria) IScoreObjectiveCriteria.INSTANCES.get(
                    nbttagcompound.getString("CriteriaName"));

            if (iscoreobjectivecriteria != null)
            {
                String s = nbttagcompound.getString("Name");

                if (s.length() > 16)
                {
                    s = s.substring(0, 16);
                }

                ScoreObjective scoreobjective = this.theScoreboard.addScoreObjective(s, iscoreobjectivecriteria);
                scoreobjective.setDisplayName(nbttagcompound.getString("DisplayName"));
                scoreobjective.setRenderType(IScoreObjectiveCriteria.EnumRenderType.func_178795_a(nbttagcompound.getString("RenderType")));
                if (scoreobjective instanceof AnimatedScoreObjective && nbttagcompound.hasKey("Animate"))
                {
                    ((AnimatedScoreObjective) scoreobjective).alwaysAnimate = nbttagcompound.getBoolean("Animate");
                }
            }
        }
    }

    @Override protected NBTTagList objectivesToNbt()
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (ScoreObjective scoreobjective : this.theScoreboard.getScoreObjectives())
        {
            if (scoreobjective.getCriteria() != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setString("Name", scoreobjective.getName());
                nbttagcompound.setString("CriteriaName", scoreobjective.getCriteria().getName());
                nbttagcompound.setString("DisplayName", scoreobjective instanceof AnimatedScoreObjective ?
                        ((AnimatedScoreObjective) scoreobjective)._GetDisplayName() :
                        scoreobjective.getDisplayName());
                if (scoreobjective instanceof AnimatedScoreObjective)
                {
                    nbttagcompound.setBoolean("Animate", ((AnimatedScoreObjective) scoreobjective).alwaysAnimate);
                }
                nbttagcompound.setString("RenderType", scoreobjective.getRenderType().func_178796_a());
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        return nbttaglist;
    }
}
