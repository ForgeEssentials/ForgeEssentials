package com.forgeessentials.multiworld;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardSaveData;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MultiworldScoreboard extends Scoreboard
{
    private final Multiworld multiworld;
    private final Set<ScoreObjective> field_96553_b = Sets.<ScoreObjective>newHashSet();
    private ScoreboardSaveData scoreboardSaveData;

    public MultiworldScoreboard(Multiworld multiworld)
    {
        this.multiworld = multiworld;
    }

    public void func_96536_a(Score p_96536_1_)
    {
        super.func_96536_a(p_96536_1_);

        if (this.field_96553_b.contains(p_96536_1_.getObjective()))
        {
            multiworld.sendPacketToAllPlayers(new S3CPacketUpdateScore(p_96536_1_));
        }

        this.markSaveDataDirty();
    }

    public void func_96516_a(String p_96516_1_)
    {
        super.func_96516_a(p_96516_1_);
        multiworld.sendPacketToAllPlayers(new S3CPacketUpdateScore(p_96516_1_));
        this.markSaveDataDirty();
    }

    public void func_178820_a(String p_178820_1_, ScoreObjective p_178820_2_)
    {
        super.func_178820_a(p_178820_1_, p_178820_2_);
        multiworld.sendPacketToAllPlayers(new S3CPacketUpdateScore(p_178820_1_, p_178820_2_));
        this.markSaveDataDirty();
    }

    /**
     * 0 is tab menu, 1 is sidebar, 2 is below name
     */
    public void setObjectiveInDisplaySlot(int p_96530_1_, ScoreObjective p_96530_2_)
    {
        ScoreObjective scoreobjective = this.getObjectiveInDisplaySlot(p_96530_1_);
        super.setObjectiveInDisplaySlot(p_96530_1_, p_96530_2_);

        if (scoreobjective != p_96530_2_ && scoreobjective != null)
        {
            if (this.func_96552_h(scoreobjective) > 0)
            {
                multiworld.sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(p_96530_1_, p_96530_2_));
            }
            else
            {
                this.sendDisplaySlotRemovalPackets(scoreobjective);
            }
        }

        if (p_96530_2_ != null)
        {
            if (this.field_96553_b.contains(p_96530_2_))
            {
                multiworld.sendPacketToAllPlayers(new S3DPacketDisplayScoreboard(p_96530_1_, p_96530_2_));
            }
            else
            {
                this.func_96549_e(p_96530_2_);
            }
        }

        this.markSaveDataDirty();
    }

    /**
     * Adds a player to the given team
     */
    public boolean addPlayerToTeam(String player, String newTeam)
    {
        if (super.addPlayerToTeam(player, newTeam))
        {
            ScorePlayerTeam scoreplayerteam = this.getTeam(newTeam);
            multiworld.sendPacketToAllPlayers(new S3EPacketTeams(scoreplayerteam, Arrays.asList(new String[] { player }), 3));
            this.markSaveDataDirty();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes the given username from the given ScorePlayerTeam. If the player is not on the team then an
     * IllegalStateException is thrown.
     */
    public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_)
    {
        super.removePlayerFromTeam(p_96512_1_, p_96512_2_);
        multiworld.sendPacketToAllPlayers(new S3EPacketTeams(p_96512_2_, Arrays.asList(new String[] { p_96512_1_ }), 4));
        this.markSaveDataDirty();
    }

    /**
     * Called when a score objective is added
     */
    public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn)
    {
        super.onScoreObjectiveAdded(scoreObjectiveIn);
        this.markSaveDataDirty();
    }

    public void onObjectiveDisplayNameChanged(ScoreObjective p_96532_1_)
    {
        super.onObjectiveDisplayNameChanged(p_96532_1_);

        if (this.field_96553_b.contains(p_96532_1_))
        {
            multiworld.sendPacketToAllPlayers(new S3BPacketScoreboardObjective(p_96532_1_, 2));
        }

        this.markSaveDataDirty();
    }

    public void onScoreObjectiveRemoved(ScoreObjective p_96533_1_)
    {
        super.onScoreObjectiveRemoved(p_96533_1_);

        if (this.field_96553_b.contains(p_96533_1_))
        {
            this.sendDisplaySlotRemovalPackets(p_96533_1_);
        }

        this.markSaveDataDirty();
    }

    /**
     * This packet will notify the players that this team is created, and that will register it on the client
     */
    public void broadcastTeamCreated(ScorePlayerTeam playerTeam)
    {
        super.broadcastTeamCreated(playerTeam);
        multiworld.sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 0));
        this.markSaveDataDirty();
    }

    /**
     * This packet will notify the players that this team is updated
     */
    public void sendTeamUpdate(ScorePlayerTeam playerTeam)
    {
        super.sendTeamUpdate(playerTeam);
        multiworld.sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 2));
        this.markSaveDataDirty();
    }

    public void func_96513_c(ScorePlayerTeam playerTeam)
    {
        super.func_96513_c(playerTeam);
        multiworld.sendPacketToAllPlayers(new S3EPacketTeams(playerTeam, 1));
        this.markSaveDataDirty();
    }

    public void func_96547_a(ScoreboardSaveData p_96547_1_)
    {
        this.scoreboardSaveData = p_96547_1_;
    }

    protected void markSaveDataDirty()
    {
        if (this.scoreboardSaveData != null)
        {
            this.scoreboardSaveData.markDirty();
        }
    }

    public List<Packet> func_96550_d(ScoreObjective p_96550_1_)
    {
        List<Packet> list = Lists.<Packet>newArrayList();
        list.add(new S3BPacketScoreboardObjective(p_96550_1_, 0));

        for (int i = 0; i < 19; ++i)
        {
            if (this.getObjectiveInDisplaySlot(i) == p_96550_1_)
            {
                list.add(new S3DPacketDisplayScoreboard(i, p_96550_1_));
            }
        }

        for (Score score : this.getSortedScores(p_96550_1_))
        {
            list.add(new S3CPacketUpdateScore(score));
        }

        return list;
    }

    public void func_96549_e(ScoreObjective p_96549_1_)
    {
        List<Packet> list = this.func_96550_d(p_96549_1_);

        for (EntityPlayerMP entityplayermp : multiworld.getPlayerList())
        {
            for (Packet packet : list)
            {
                entityplayermp.playerNetServerHandler.sendPacket(packet);
            }
        }

        this.field_96553_b.add(p_96549_1_);
    }

    public List<Packet> func_96548_f(ScoreObjective p_96548_1_)
    {
        List<Packet> list = Lists.<Packet>newArrayList();
        list.add(new S3BPacketScoreboardObjective(p_96548_1_, 1));

        for (int i = 0; i < 19; ++i)
        {
            if (this.getObjectiveInDisplaySlot(i) == p_96548_1_)
            {
                list.add(new S3DPacketDisplayScoreboard(i, p_96548_1_));
            }
        }

        return list;
    }

    public void sendDisplaySlotRemovalPackets(ScoreObjective p_96546_1_)
    {
        List<Packet> list = this.func_96548_f(p_96546_1_);

        for (EntityPlayerMP entityplayermp : multiworld.getPlayerList())
        {
            for (Packet packet : list)
            {
                entityplayermp.playerNetServerHandler.sendPacket(packet);
            }
        }

        this.field_96553_b.remove(p_96546_1_);
    }

    public int func_96552_h(ScoreObjective p_96552_1_)
    {
        int i = 0;

        for (int j = 0; j < 19; ++j)
        {
            if (this.getObjectiveInDisplaySlot(j) == p_96552_1_)
            {
                ++i;
            }
        }

        return i;
    }
}

