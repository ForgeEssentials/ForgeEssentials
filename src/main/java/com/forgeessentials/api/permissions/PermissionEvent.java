package com.forgeessentials.api.permissions;

import com.forgeessentials.api.UserIdent;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * PermissionEvent
 * 
 * Parent event for all permission related events. The dispatched sub-events are:
 * <ul>
 * <li>{@link PermissionEvent.Initialize}
 * <li>{@link PermissionEvent.AfterLoad}
 * <li>{@link PermissionEvent.BeforeSave}
 * <li>{@link PermissionEvent.User.ModifyPermission}
 * <li>{@link PermissionEvent.User.ModifyGroups}
 * <li>{@link PermissionEvent.Group.ModifyPermission}
 * <li>{@link PermissionEvent.Group.Create}
 * <li>{@link PermissionEvent.Group.Delete}
 * <li>{@link PermissionEvent.Zone.Create}
 * <li>{@link PermissionEvent.Zone.Delete}
 * </ul>
 */
public class PermissionEvent extends Event
{

    public ServerZone serverZone;

    public PermissionEvent(ServerZone serverZone)
    {
        this.serverZone = serverZone;
    }

    /**
     * Event runs when a new permission-tree (ServerZone) gets initialized. This event can be used to initialize
     * internal groups or some other default permissions.
     */
    public static class Initialize extends PermissionEvent
    {
        public Initialize(ServerZone serverZone)
        {
            super(serverZone);
        }
    }

    /**
     * Event after permissions have been reloaded
     */
    public static class AfterLoad extends PermissionEvent
    {
        public AfterLoad(ServerZone serverZone)
        {
            super(serverZone);
        }
    }

    /**
     * Event before permissions are saved
     */
    public static class BeforeSave extends PermissionEvent
    {
        public BeforeSave(ServerZone serverZone)
        {
            super(serverZone);
        }
    }

    /**
     * Parent class for user-related permission events
     */
    public static class User extends PermissionEvent
    {

        public UserIdent ident;

        public User(ServerZone serverZone, UserIdent ident)
        {
            super(serverZone);
            this.ident = ident;
        }

        @Cancelable
        public static class ModifyPermission extends User
        {

            public com.forgeessentials.api.permissions.Zone zone;
            public String permissionNode;
            public String value;

            public ModifyPermission(ServerZone serverZone, UserIdent ident, com.forgeessentials.api.permissions.Zone zone, String permissionNode, String value)
            {
                super(serverZone, ident);
                this.zone = zone;
                this.permissionNode = permissionNode;
                this.value = value;
            }
        }

        @Cancelable
        public static class ModifyGroups extends User
        {

            public static enum Action
            {
                ADD, REMOVE;
            }

            public Action action;
            public String group;

            public ModifyGroups(ServerZone serverZone, UserIdent ident, Action action, String group)
            {
                super(serverZone, ident);
                this.action = action;
                this.group = group;
            }
        }

    }

    /**
     * Parent class for group-related permission events
     */
    public static class Group extends PermissionEvent
    {

        public String group;

        public Group(ServerZone serverZone, String group)
        {
            super(serverZone);
            this.group = group;
        }

        @Cancelable
        public static class ModifyPermission extends Group
        {

            public com.forgeessentials.api.permissions.Zone zone;
            public String permissionNode;
            public String value;

            public ModifyPermission(ServerZone serverZone, String group, com.forgeessentials.api.permissions.Zone zone, String permissionNode, String value)
            {
                super(serverZone, group);
                this.zone = zone;
                this.permissionNode = permissionNode;
                this.value = value;
            }
        }

        @Cancelable
        public static class Create extends Group
        {
            public Create(ServerZone serverZone, String group)
            {
                super(serverZone, group);
            }
        }

        @Cancelable
        public static class Delete extends Group
        {
            public Delete(ServerZone serverZone, String group)
            {
                super(serverZone, group);
            }
        }

    }

    /**
     * Parent class for group-related permission events
     */
    public static class Zone extends PermissionEvent
    {

        public com.forgeessentials.api.permissions.Zone zone;

        public Zone(ServerZone serverZone, com.forgeessentials.api.permissions.Zone zone)
        {
            super(serverZone);
            this.zone = zone;
        }

        @Cancelable
        public static class Create extends Zone
        {
            public Create(ServerZone serverZone, com.forgeessentials.api.permissions.Zone zone)
            {
                super(serverZone, zone);
            }
        }

        @Cancelable
        public static class Delete extends Zone
        {
            public Delete(ServerZone serverZone, com.forgeessentials.api.permissions.Zone zone)
            {
                super(serverZone, zone);
            }
        }

    }

}