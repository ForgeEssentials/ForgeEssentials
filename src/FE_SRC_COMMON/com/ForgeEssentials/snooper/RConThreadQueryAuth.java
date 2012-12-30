package com.ForgeEssentials.snooper;

import java.net.DatagramPacket;
import java.util.Date;
import java.util.Random;

class RConThreadQueryAuth
{
    /** The creation timestamp for this auth */
    private long timestamp;

    /** A random challenge */
    private int randomChallenge;

    /** A client-provided request ID associated with this query */
    private byte[] requestId;

    /** A unique string of bytes used to verify client auth */
    private byte[] challengeValue;

    /** The request ID stored as a String */
    private String requestIdAsString;

    /** The RConThreadQuery that this is probably an inner class of */
    final RConQueryThread queryThread;

    public RConThreadQueryAuth(RConQueryThread rConQueryThread, DatagramPacket par2DatagramPacket)
    {
        this.queryThread = rConQueryThread;
        this.timestamp = (new Date()).getTime();
        byte[] var3 = par2DatagramPacket.getData();
        this.requestId = new byte[4];
        this.requestId[0] = var3[3];
        this.requestId[1] = var3[4];
        this.requestId[2] = var3[5];
        this.requestId[3] = var3[6];
        this.requestIdAsString = new String(this.requestId);
        this.randomChallenge = (new Random()).nextInt(16777216);
        this.challengeValue = String.format("\t%s%d\u0000", new Object[] {this.requestIdAsString, Integer.valueOf(this.randomChallenge)}).getBytes();
    }

    /**
     * Returns true if the auth's creation timestamp is less than the given time, otherwise false
     */
    public Boolean hasExpired(long par1)
    {
        return Boolean.valueOf(this.timestamp < par1);
    }

    /**
     * Returns the random challenge number assigned to this auth
     */
    public int getRandomChallenge()
    {
        return this.randomChallenge;
    }

    /**
     * Returns the auth challenge value
     */
    public byte[] getChallengeValue()
    {
        return this.challengeValue;
    }

    /**
     * Returns the request ID provided by the client.
     */
    public byte[] getRequestId()
    {
        return this.requestId;
    }
}
