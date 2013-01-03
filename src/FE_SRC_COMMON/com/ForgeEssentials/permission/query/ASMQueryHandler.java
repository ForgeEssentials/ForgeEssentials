package com.ForgeEssentials.permission.query;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_6;

import com.ForgeEssentials.permission.query.PermQuery.PermResult;

import java.lang.reflect.Method;

import net.minecraftforge.event.EventPriority;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class ASMQueryHandler implements IQueryListener
{
	private static int					IDs					= 0;
	private static final String			HANDLER_DESC		= Type.getInternalName(IQueryListener.class);
	private static final String			HANDLER_FUNC_DESC	= Type.getMethodDescriptor(IQueryListener.class.getDeclaredMethods()[0]);
	private static final FEASMClassLoader	LOADER				= new FEASMClassLoader();

	private final IQueryListener		handler;
	private final PermSubscribe			subInfo;

	public ASMQueryHandler(Object target, Method method) throws Exception
	{
		handler = (IQueryListener) createWrapper(method).getConstructor(Object.class).newInstance(target);
		subInfo = method.getAnnotation(PermSubscribe.class);
	}

	@Override
	public void invoke(PermQuery query)
	{
		if (handler != null)
				handler.invoke(query);
	}

	public EventPriority getPriority()
	{
		return subInfo.priority();
	}

	public Class<?> createWrapper(Method callback)
	{
		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		String name = getUniqueName(callback);
		String desc = name.replace('.', '/');
		String instType = Type.getInternalName(callback.getDeclaringClass());
		String eventType = Type.getInternalName(callback.getParameterTypes()[0]);

		cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER, desc, null, "java/lang/Object", new String[] { HANDLER_DESC });

		cw.visitSource(".dynamic", null);
		{
			cw.visitField(ACC_PUBLIC, "instance", "Ljava/lang/Object;", null, null).visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, desc, "instance", "Ljava/lang/Object;");
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "invoke", HANDLER_FUNC_DESC, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, desc, "instance", "Ljava/lang/Object;");
			mv.visitTypeInsn(CHECKCAST, instType);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, eventType);
			mv.visitMethodInsn(INVOKEVIRTUAL, instType, callback.getName(), Type.getMethodDescriptor(callback));
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		cw.visitEnd();
		return LOADER.define(name, cw.toByteArray());
	}

	private String getUniqueName(Method callback)
	{
		return String.format("%s_%d_%s_%s_%s", getClass().getName(), IDs++, callback.getDeclaringClass().getSimpleName(), callback.getName(), callback.getParameterTypes()[0].getSimpleName());
	}

	private static class FEASMClassLoader extends ClassLoader
	{
		private FEASMClassLoader()
		{
			super(FEASMClassLoader.class.getClassLoader());
		}

		public Class<?> define(String name, byte[] data)
		{
			return defineClass(name, data, 0, data.length);
		}
	}
}
