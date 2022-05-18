package com.forgeessentials.client.handler;


public class ReachDistanceHandler
{

    private static float reachDistance = 0;

    public static float getReachDistance()
    {
        return reachDistance;
    }
    public static void setReachDistance(float dis)
    {
        if(dis<0) {dis = Math.abs(dis);}
        reachDistance = dis;
    }
}
