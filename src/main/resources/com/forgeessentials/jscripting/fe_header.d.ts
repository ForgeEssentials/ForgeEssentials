
declare namespace fe {

    type CommandCallback = (args: fe.CommandArgs) => void;

}

declare namespace net.minecraftforge.permission {
    enum PermissionLevel {
        TRUE,
        OP,
        FALSE,
    }
}

declare namespace com.forgeessentials.commons.selections {
    enum AreaShape {
        BOX,
        ELLIPSOID,
        CYLINDER,
    }
}

declare function createPoint(x: int, y: int, z: int);
declare function createWorldPoint(dim: int, x: int, y: int, z: int);
