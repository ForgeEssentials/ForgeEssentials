package com.forgeessentials.core.preloader.asminjector;

/**
 * This dummy interface is used to generate return statements inside injectors. <br>
 * Injectors <b>must never</b> call the wrong {@link CallbackInfo#doReturn()} function or injection will fail.
 * 
 * Always use the one which matches the return type of the original function.
 */
public interface CallbackInfo
{

    public void doReturn();

    public void doReturn(boolean value);

    public void doReturn(byte value);

    public void doReturn(short value);

    public void doReturn(int value);

    public void doReturn(long value);

    public void doReturn(float value);

    public void doReturn(double value);

    public void doReturn(Object value);

    public void doReturn(boolean[] value);

    public void doReturn(byte[] value);

    public void doReturn(short[] value);

    public void doReturn(int[] value);

    public void doReturn(long[] value);

    public void doReturn(float[] value);

    public void doReturn(double[] value);

    public void doReturn(Object[] value);

}