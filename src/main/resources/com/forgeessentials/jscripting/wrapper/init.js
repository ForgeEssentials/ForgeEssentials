
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

// NBT handling
function getNbt(e) {
	return JSON.parse(e._getNbt());
}
function setNbt(e, d) {
	e._setNbt(JSON.stringify(d));
}

// window utilities
function createAxisAlignedBB(arg1, arg2, arg3, arg4, arg5, arg6) {
	return new mc.util.AxisAlignedBB(arg1, arg2, arg3, arg4, arg5, arg6);
}
