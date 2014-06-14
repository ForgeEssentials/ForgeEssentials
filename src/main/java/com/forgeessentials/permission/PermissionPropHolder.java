package com.forgeessentials.permission;

public class PermissionPropHolder extends PermissionProp {
    public final String target;
    public final String zone;

    public PermissionPropHolder(String target, String qualifiedName, String prop, String zone)
    {
        super(qualifiedName, prop);
        this.target = target;
        this.zone = zone;
    }

}