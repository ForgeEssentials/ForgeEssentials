package com.forgeessentials.permissions;

/**
 * @author AbrarSyed
 */
public class Permission extends PermissionChecker {
    public static final String ALL = "_ALL_";

    public boolean allowed;

    public Permission(String qualifiedName, boolean allowed)
    {
        super(qualifiedName);
        this.allowed = allowed;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Permission)
        {
            Permission perm = (Permission) object;
            return super.equals(object) && allowed == perm.allowed;
        }
        else
        {
            return super.equals(object);
        }
    }

    @Override
    public String toString()
    {
        return super.toString() + " : " + allowed;
    }
}
