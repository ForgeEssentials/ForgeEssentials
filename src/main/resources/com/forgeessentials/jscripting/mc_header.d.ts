
declare type byte = number;
declare type int = number;
declare type long = number;
declare type float = number;
declare type double = number;

declare function getNbt(entity: mc.entity.Entity | mc.item.ItemStack): any;
declare function setNbt(entity: mc.entity.Entity | mc.item.ItemStack, data: any);

declare function setTimeout(handler: (...args: any[]) => void, timeout?: any, ...args: any[]): number;
declare function setInterval(handler: (...args: any[]) => void, interval?: any, ...args: any[]): number;
declare function clearTimeout(handle: int): void;
declare function clearInterval(handle: int): void;

declare function createAxisAlignedBB(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double);

/**
 * Constants that tell getNbt and setNbt the types of entries. Use nbt[NBT_INT + 'myVar'] for access
 */ 
declare const NBT_BYTE: string;
declare const NBT_SHORT: string;
declare const NBT_INT: string;
declare const NBT_LONG: string;
declare const NBT_FLOAT: string;
declare const NBT_DOUBLE: string;
declare const NBT_BYTE_ARRAY: string;
declare const NBT_STRING: string;
declare const NBT_COMPOUND: string;
declare const NBT_INT_ARRAY: string;

declare abstract class JavaList<T> extends Array<T> {
    size(): int;
    isEmpty(): boolean;
    toArray(): any[];
    get(index: int): T;
    add(element: T): T;
    set(index: int, element: T): T;
    clear(): void;
    remove(index: int): T;
    remove(element: T): boolean;
}

declare namespace cpw.mods.fml.common.eventhandler.Event {
    enum Result {
        DENY,
        DEFAULT,
        ALLOW
    }
}

declare namespace cpw.mods.fml.common.eventhandler {
    enum EventPriority {
        HIGHEST, // First to execute
        HIGH,
        NORMAL,
        LOW,
        LOWEST // Last to execute
    }
}
