package net.minecraftforge.permission;

public enum PermissionLevel
{

    TRUE(0), OP_1(1), OP_2(2), OP_3(3), OP(4), FALSE(5);

    private int opLevel;

    private PermissionLevel(int opLevel)
    {
        this.opLevel = opLevel;
    }

    public int getOpLevel()
    {
        return opLevel;
    }

    public static PermissionLevel fromBoolean(boolean value)
    {
        return value ? TRUE : FALSE;
    }

    public static PermissionLevel fromInteger(int value)
    {
        switch (value)
        {
        case 0:
            return TRUE;
        case 1:
            return OP_1;
        case 2:
            return OP_2;
        case 3:
            return OP_3;
        case 4:
            return OP;
        default:
            return FALSE;
        }
    }

}
