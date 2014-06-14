package com.forgeessentials.permission;

public class PermissionProp extends PermissionChecker {
    public final String value;

    public PermissionProp(String qualifiedName, String value)
    {
        super(qualifiedName);
        this.value = value;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof PermissionProp)
        {
            PermissionProp perm = (PermissionProp) object;
            return super.equals(object) && value.equals(perm.value);
        }
        else
        {
            return super.equals(object);
        }
    }

    @Override
    public String toString()
    {
        return super.toString() + " = " + value;
    }
}
