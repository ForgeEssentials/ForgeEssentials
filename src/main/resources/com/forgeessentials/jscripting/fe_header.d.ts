
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

declare function createPoint(x: int, y: int, z: int);
declare function createWorldPoint(dim: int, x: int, y: int, z: int);
