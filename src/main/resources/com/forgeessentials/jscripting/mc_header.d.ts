
declare type char = string;
declare type byte = number;
declare type int = number;
declare type long = number;
declare type float = number;
declare type double = number;

declare function require(path: string): any;
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

declare namespace net.minecraftforge.fml.common.eventhandler.Event {
    enum Result {
        DENY,
        DEFAULT,
        ALLOW
    }
}

declare namespace net.minecraftforge.fml.common.eventhandler {
    enum EventPriority {
        HIGHEST, // First to execute
        HIGH,
        NORMAL,
        LOW,
        LOWEST // Last to execute
    }
}

declare namespace java.util {
	interface GenericCollection<E> {
	}
    interface Collection extends GenericCollection<any> {
    }
	interface GenericList<E> extends GenericCollection<E> {
		size(): int;
		isEmpty(): boolean;
		contains(arg0: any): boolean;
		// iterator(): Iterator;
		toArray(): any[];
		toArray(arg0: E[]): E[];
		add(arg0: E): boolean;
		remove(arg0: any): boolean;
		containsAll(arg0: Collection | GenericCollection<E>): boolean;
		addAll(arg0: Collection | GenericCollection<E>): boolean;
		addAll(arg0: int, arg1: Collection | GenericCollection<E>): boolean;
		removeAll(arg0: Collection | GenericCollection<E>): boolean;
		retainAll(arg0: Collection | GenericCollection<E>): boolean;
		// replaceAll(arg0: UnaryOperator): void;
		// sort(arg0: Comparator): void;
		clear(): void;
		equals(arg0: any): boolean;
		hashCode(): int;
		get(arg0: int): E;
		set(arg0: int, arg1: E): E;
		add(arg0: int, arg1: E): void;
		remove(arg0: int): E;
		indexOf(arg0: any): int;
		lastIndexOf(arg0: any): int;
		// listIterator(): ListIterator;
		// listIterator(arg0: int): ListIterator;
		subList(arg0: int, arg1: int): GenericList<E>;
		// spliterator(): Spliterator;
	}
    interface List extends GenericList<any> {
    }

    interface GenericSet<T> extends GenericCollection<T> {
    }
    interface Set extends GenericSet<any> {
    }

    interface GenericMap<TKey, TValue> {
    }
    interface Map extends GenericMap<any, any> {
    }

    namespace Locale {
        interface Category {
        }
        interface FilteringMode {
        }
    }
}

declare namespace java.time {
    interface Instant {
    }
    interface ZoneId {
    }
}

declare namespace java.lang {
    class Enum {
        toString(): string;
        equals(obj: any): boolean;
    }
}


declare namespace net.minecraft.entity.player {
    interface PlayerCapabilities {
    }
}
