
var exports = {};

// NBT constants
var NBT_BYTE = 'b:';
var NBT_SHORT = 's:';
var NBT_INT = 'i:';
var NBT_LONG = 'l:';
var NBT_FLOAT = 'f:';
var NBT_DOUBLE = 'd:';
var NBT_BYTE_ARRAY = 'B:';
var NBT_STRING = 'S:';
var NBT_COMPOUND = 'c:';
var NBT_INT_ARRAY = 'I:';

// PermissionLevel constants
var PERMLEVEL_TRUE = " + PermissionLevel.TRUE.getOpLevel() + ";
var PERMLEVEL_OP = " + PermissionLevel.OP.getOpLevel() + ";
var PERMLEVEL_FALSE = " + PermissionLevel.FALSE.getOpLevel() + ";

// timeouts
function setTimeout(fn, t, args) {
	return window.setTimeout(fn, t, args);
}
function setInterval(fn, t, args) {
	return window.setInterval(fn, t, args);
}
function clearTimeout(id) {
	return window.clearTimeout(id);
}
function clearInterval(id) {
	return window.clearInterval(id);
}

// window utilities
function createAxisAlignedBB(arg1, arg2, arg3, arg4, arg5, arg6) {
	return window.createAxisAlignedBB(arg1, arg2, arg3, arg4, arg5, arg6);
}
function createPoint(arg1, arg2, arg3) {
	return window.createPoint(arg1, arg2, arg3);
}
function createWorldPoint(arg1, arg2, arg3, arg4) {
	return window.createWorldPoint(arg1, arg2, arg3, arg4);
}

// NBT handling
function getNbt(e) {
	return JSON.parse(e._getNbt());
}
function setNbt(e, d) {
	e._setNbt(JSON.stringify(d));
}
