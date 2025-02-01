package com.forgeessentials.core.moduleLauncher;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.collect.HashMultimap;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CallableMap
{

    private HashMultimap<String, FECallable> callables;

    public CallableMap()
    {
        callables = HashMultimap.create();
    }

    public void scanObject(Object obj)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            FECallable call;
            Class c = obj.getClass();
            if (obj instanceof ModContainer)
            {
                c = ((ModContainer) obj).getMod().getClass();
            }
            else if (obj instanceof ModuleContainer)
            {
                c = ((ModuleContainer) obj).module.getClass();
            }

            for (Method m : c.getDeclaredMethods())
            {
                if (m.isAnnotationPresent(SideOnly.class))
                {
                    SideOnly annot = m.getAnnotation(SideOnly.class);
                    if (!annot.value().equals(FMLCommonHandler.instance().getSide()))
                    {
                        continue;
                    }
                }

                if (Modifier.isStatic(m.getModifiers()))
                {
                    call = new FECallable(m);
                }
                else
                {
                    call = new FECallable(m, obj);
                }

                for (Annotation annot : m.getAnnotations())
                {
                    String name = annot.annotationType().getName();
                    callables.put(name, call);
                }
            }
        }
        catch (Throwable e)
        {
            LoggingHandler.felog.error("Error stripping methods from class! " + obj.getClass().getName());
        }
    }

    public void scanClass(Class<?> c)
    {
        if (c == null)
        {
            return;
        }

        try
        {
            FECallable call;

            for (Method m : c.getDeclaredMethods())
            {
                if (m.isAnnotationPresent(SideOnly.class))
                {
                    SideOnly annot = m.getAnnotation(SideOnly.class);
                    if (!annot.value().equals(FMLCommonHandler.instance().getSide()))
                    {
                        continue;
                    }
                }

                if (!Modifier.isStatic(m.getModifiers()))
                {
                    continue;
                }

                call = new FECallable(m);

                for (Annotation annot : m.getAnnotations())
                {
                    String name = annot.annotationType().getName();
                    callables.put(name, call);
                }
            }
        }
        catch (Throwable e)
        {
            LoggingHandler.felog.error("Error stripping methods from class! " + c.getName());
        }
    }

    public Set<FECallable> getCallable(Class<? extends Annotation> annot)
    {
        return callables.get(annot.getName());
    }

    public Set<FECallable> getCallable(String annotName)
    {
        return callables.get(annotName);
    }

    public static final class FECallable
    {
        private Method method;
        private Object instance = null;
        private String ident;

        FECallable(Method m, Object instance)
        {
            this(m);

            if (instance == null)
            {
                this.instance = instance;
            }
            else if (instance instanceof ModContainer)
            {
                this.instance = ((ModContainer) instance).getMod();
                ident = ((ModContainer) instance).getModId();
            }
            else if (instance instanceof ModuleContainer)
            {
                this.instance = ((ModuleContainer) instance).module;
                ident = ((ModuleContainer) instance).name;
            }
            else
            {
                this.instance = instance;
            }

        }

        FECallable(Method m)
        {
            method = m;

            Class<?> c = m.getDeclaringClass();
            if (c.isAnnotationPresent(Mod.class))
            {
                ident = c.getAnnotation(Mod.class).modid();
            }
            else if (c.isAnnotationPresent(FEModule.class))
            {
                ident = c.getAnnotation(FEModule.class).name();
            }
            else if (c.isAnnotationPresent(ForgeEssentialsRegistrar.class))
            {
                ident = c.getAnnotation(ForgeEssentialsRegistrar.class).ident();
            }
            else
            {
                ident = "UNKNOWN";
            }
        }

        public boolean isStatic()
        {
            return instance == null;
        }

        public boolean hasReturn()
        {
            return !method.getReturnType().equals(void.class);
        }

        public Class<?> getReturn()
        {
            return method.getReturnType();
        }

        public Class<?>[] getParameters()
        {
            return method.getParameterTypes();
        }

        public Object call(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
        {
            method.setAccessible(true);
            return method.invoke(instance, args);
        }

        public Annotation getAnnotation(Class annot)
        {
            return method.getAnnotation(annot);
        }

        public Annotation getClassAnnotation(Class annot)
        {
            return method.getDeclaringClass().getAnnotation(annot);
        }

        public String getIdent()
        {
            return ident;
        }

    }

}
