
/**
 * Constants for permission level used when registering permissions
 */ 
declare enum PermissionLevel {
    TRUE, OP, FALSE
}

declare namespace mc {
    type CommandCallback = (args: fe.CommandArgs) => void;
}

declare namespace net.minecraftforge.permission {
    enum PermissionLevel {
        TRUE,
        OP,
        FALSE,
    }
}

declare function createPoint(x: int, y: int, z: int);
declare function createWorldPoint(dim: int, x: int, y: int, z: int);
