package com.forgeessentials.jscripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

import org.apache.commons.lang3.ArrayUtils;

import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TaskRegistry.RunLaterTimerTask;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.jscripting.wrapper.JsBlockStatic;
import com.forgeessentials.jscripting.wrapper.JsCommandArgs;
import com.forgeessentials.jscripting.wrapper.JsItemStatic;
import com.forgeessentials.jscripting.wrapper.JsServerStatic;
import com.forgeessentials.jscripting.wrapper.JsWorldStatic;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class ScriptInstance
{

    public static class ProptertiesInfo<T>
    {

        public final Class<T> clazz;

        public final List<Field> fields = new ArrayList<>();

        public final CompiledScript script;

        public ProptertiesInfo(Class<T> clazz) throws ScriptException
        {
            this.clazz = clazz;
            for (Field field : clazz.getDeclaredFields())
            {
                if (field.isAccessible())
                {
                    fields.add(field);
                }
            }
            String scriptSrc = fields.stream().map((f) -> "o." + f.getName()).collect(Collectors.joining(",", "[", "]"));
            script = propertyEngine.compile(scriptSrc);
        }

        public ProptertiesInfo(Class<T> clazz, T instance) throws ScriptException
        {
            this.clazz = clazz;
            for (Field field : clazz.getDeclaredFields())
            {
                if ((field.getModifiers() & Modifier.PUBLIC) != 0)
                {
                    try
                    {
                        field.setAccessible(true);
                        field.set(instance, null);
                        fields.add(field);
                    }
                    catch (IllegalArgumentException | IllegalAccessException e)
                    {
                        // field will be ignored!
                        System.out.println("Ignoring deserialization field " + field.getName());
                    }
                }
            }
            String scriptSrc = fields.stream().map((f) -> "o." + f.getName()).collect(Collectors.joining(",", "[", "]"));
            script = propertyEngine.compile(scriptSrc);
        }
    }

    // private static WeakReference<ScriptInstance> lastActive;
    private static ScriptInstance lastActive;

    private static Compilable propertyEngine = ModuleJScripting.getCompilable();

    private static Map<String, CompiledScript> propertyScripts = new HashMap<>();

    private static Map<Class<?>, ProptertiesInfo<?>> propertyInfos = new HashMap<>();

    private File file;

    private long lastModified;

    private CompiledScript script;

    private Invocable invocable;

    private SimpleBindings getPropertyBindings = new SimpleBindings();

    private Set<String> illegalFunctions = new HashSet<>();

    private Map<Integer, TimerTask> tasks = new HashMap<>();

    private WeakReference<ICommandSender> lastSender;

    public ScriptInstance(File file) throws IOException, ScriptException
    {
        if (!file.exists())
            throw new IllegalArgumentException("file");

        this.file = file;
        compileScript();
    }

    protected void compileScript() throws IOException, FileNotFoundException, ScriptException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            // Load and compile script
            script = ModuleJScripting.getCompilable().compile(reader);

            // Initialization of module environment
            script.getEngine().put("Server", new JsServerStatic(this));
            script.getEngine().put("Block", new JsBlockStatic());
            script.getEngine().put("Item", new JsItemStatic());
            script.getEngine().put("World", new JsWorldStatic());
            script.getEngine().eval("" +
                    "var exports = {};" +
                    // NBT constants
                    "var NBT_BYTE = 'b:';" +
                    "var NBT_SHORT = 's:';" +
                    "var NBT_INT = 'i:';" +
                    "var NBT_LONG = 'l:';" +
                    "var NBT_FLOAT = 'f:';" +
                    "var NBT_DOUBLE = 'd:';" +
                    "var NBT_BYTE_ARRAY = 'B:';" +
                    "var NBT_STRING = 'S:';" +
                    "var NBT_COMPOUND = 'c:';" +
                    "var NBT_INT_ARRAY = 'I:';" +
                    // timeouts
                    "function setTimeout(fn, t, args) { return Server.setTimeout(fn, t, args); };" +
                    "function setInterval(fn, t, args) { return Server.setInterval(fn, t, args); };" +
                    "function clearTimeout(id) { return Server.clearTimeout(id); };" +
                    "function clearInterval(id) { return Server.clearInterval(id); };" +
                    // NBT handling
                    "function getNbt(e) { return JSON.parse(e._getNbt()); }" +
                    "function setNbt(e, d) { e._setNbt(JSON.stringify(d)); }" +
                    "" +
                    "");

            // Start script
            script.eval();
            // script.getEngine().get("exports")

            invocable = (Invocable) script.getEngine();
            illegalFunctions.clear();
            lastModified = file.lastModified();
        }
    }

    public void checkIfModified() throws IOException, FileNotFoundException, ScriptException
    {
        if (file.exists() && file.lastModified() != lastModified)
            compileScript();
    }

    /* ************************************************************ */
    /* Script invocation */

    public Object callGlobal(String fn, Object... args) throws NoSuchMethodException, ScriptException, CommandException
    {
        try
        {
            setLastActive();
            return this.invocable.invokeFunction(fn, args);
        }
        catch (Exception e)
        {
            illegalFunctions.add(fn);
            throw e;
        }
        finally
        {
            clearLastActive();
        }
    }

    public Object tryCallGlobal(String fn, Object... args) throws ScriptException
    {
        try
        {
            setLastActive();
            return this.invocable.invokeFunction(fn, args);
        }
        catch (NoSuchMethodException e)
        {
            illegalFunctions.add(fn);
            return null;
        }
        finally
        {
            clearLastActive();
        }
    }

    public boolean hasGlobalCallFailed(String fnName)
    {
        try
        {
            setLastActive();
            return illegalFunctions.contains(fnName);
        }
        finally
        {
            clearLastActive();
        }
    }

    public Object call(Object fn, Object thiz, Object... args) throws NoSuchMethodException, ScriptException
    {
        try
        {
            setLastActive();
            return this.invocable.invokeMethod(fn, "call", ArrayUtils.add(args, 0, thiz));
        }
        finally
        {
            clearLastActive();
        }
    }

    public Object tryCall(Object fn, Object thiz, Object... args) throws ScriptException
    {
        try
        {
            setLastActive();
            return this.invocable.invokeMethod(fn, "call", ArrayUtils.add(args, 0, thiz));
        }
        catch (NoSuchMethodException e)
        {
            return null;
        }
        finally
        {
            clearLastActive();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(Object object, String property) throws ScriptException
    {
        property = "o." + property;
        getPropertyBindings.put("o", object);
        CompiledScript propertyScript = propertyScripts.get(property);
        if (propertyScript == null)
        {
            propertyScript = propertyEngine.compile(property);
            propertyScripts.put(property, propertyScript);
        }
        return (T) propertyScript.eval(getPropertyBindings);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperties(T instance, Object object, Class<T> clazz) throws ScriptException
    {
        ProptertiesInfo<T> props = (ProptertiesInfo<T>) propertyInfos.get(clazz);
        if (props == null)
        {
            props = new ProptertiesInfo<>(clazz, instance);
            propertyInfos.put(clazz, props);
        }

        if (object instanceof Bindings)
        {
            Bindings bindings = (Bindings) object;
            try
            {
                for (Field f : props.fields)
                    f.set(instance, bindings.get(f.getName()));
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            getPropertyBindings.put("o", object);
            Object eval = props.script.eval(getPropertyBindings);
            if (!(eval instanceof Bindings))
                throw new ScriptException("Unable to access properties");
            Bindings bindings = (Bindings) eval;
            try
            {
                for (int i = 0; i < props.fields.size(); i++)
                    props.fields.get(i).set(instance, bindings.get(i));
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private void setLastActive()
    {
        // lastActive = new WeakReference<ScriptInstance>(this);
        lastActive = this;
    }

    private void clearLastActive()
    {
        lastActive = null;
    }

    public ScriptInstance getLastActive()
    {
        return lastActive;
    }

    /* ************************************************************ */
    /* Timeout & Promise handling */

    private RunLaterTimerTask createCallbackTask(Object fn, Object... args)
    {
        return new RunLaterTimerTask(() -> {
            try
            {
                call(fn, fn, args);
            }
            catch (NoSuchMethodException | ScriptException e)
            {
                chatError("Error in callback: " + e.getMessage());
            }
        });
    }

    private int registerTimeout(TimerTask task)
    {
        int id = (int) Math.round(Math.random() * Integer.MAX_VALUE);
        while (tasks.containsKey(id))
            id = (int) Math.round(Math.random() * Integer.MAX_VALUE);
        tasks.put(id, task);
        return id;
    }

    public int setTimeout(Object fn, long timeout, Object... args)
    {
        TimerTask task = createCallbackTask(fn, args);
        TaskRegistry.schedule(task, timeout);
        return registerTimeout(task);
    }

    public int setInterval(Object fn, long timeout, Object... args)
    {
        TimerTask task = createCallbackTask(fn, args);
        TaskRegistry.scheduleRepeated(task, timeout);
        return registerTimeout(task);
    }

    public void clearTimeout(int id)
    {
        TimerTask task = tasks.remove(id);
        if (task != null)
            TaskRegistry.remove(task);
    }

    public void clearInterval(int id)
    {
        clearTimeout(id);
    }

    /* ************************************************************ */
    /* Event handling */

    public void registerEventHandler(String event, Object handler)
    {
        System.out.println("TODO!");
    }

    /* ************************************************************ */
    /* Other & Utility */

    public File getFile()
    {
        return file;
    }

    public void runCommand(CommandParserArgs arguments) throws CommandException
    {
        try
        {
            callGlobal(arguments.isTabCompletion ? "tabComplete" : "processCommand", new JsCommandArgs(arguments));
        }
        catch (CommandException e)
        {
            throw e;
        }
        catch (NoSuchMethodException e)
        {
            if (!arguments.isTabCompletion)
                throw new TranslatedCommandException("Script missing processCommand function.");
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
            throw new TranslatedCommandException("Error in script: %s", e.getMessage());
        }
    }

    /* ************************************************************ */

    /**
     * Tries to send an error message to the last player using this script.<br>
     * If no player can be determined, the message will be broadcasted.
     *
     * @param message
     */
    public void chatError(String message)
    {
        chatError(lastSender == null ? null : lastSender.get(), message);
    }

    public void chatError(ICommandSender sender, String message)
    {
        IChatComponent msg = ChatOutputHandler.error(message);
        if (sender == null)
            ChatOutputHandler.broadcast(msg); // TODO: Replace with broadcast to admins only
        else
            ChatOutputHandler.sendMessage(sender, msg);
    }

    /**
     * This should be called every time a script is invoked by a user to send errors to the correct user
     *
     * @param sender
     */
    public void setLastSender(ICommandSender sender)
    {
        this.lastSender = new WeakReference<>(sender);
    }

}
