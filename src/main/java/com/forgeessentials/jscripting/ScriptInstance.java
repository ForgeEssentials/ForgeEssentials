package com.forgeessentials.jscripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
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
import net.minecraft.util.text.ITextComponent;

import org.apache.commons.lang3.ArrayUtils;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TaskRegistry.RunLaterTimerTask;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.jscripting.command.CommandJScriptCommand;
import com.forgeessentials.jscripting.wrapper.mc.event.JsEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Charsets;

public class ScriptInstance
{

    public static final String SCRIPT_ERROR_TEXT = "Script error: ";

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
            script = ScriptInstance.propertyEngine.compile(scriptSrc);
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
            script = ScriptInstance.propertyEngine.compile(scriptSrc);
        }
    }

    private static ScriptInstance lastActive;

    /* ************************************************************ */

    private File file;

    private long lastModified;

    private CompiledScript script;

    private Invocable invocable;

    @SuppressWarnings("unused")
    private Bindings exports;

    private Set<String> illegalFunctions = new HashSet<>();

    private Map<Integer, TimerTask> tasks = new HashMap<>();

    private List<CommandJScriptCommand> commands = new ArrayList<>();

    private Map<Object, JsEvent<?>> eventHandlers = new HashMap<>();

    private WeakReference<ICommandSender> lastSender;

    /* ************************************************************ */
    /* PROPERTY ACCESSING */

    private static Compilable propertyEngine = ModuleJScripting.getCompilable();

    private static Map<String, CompiledScript> propertyScripts = new HashMap<>();

    private static Map<Class<?>, ProptertiesInfo<?>> propertyInfos = new HashMap<>();

    private SimpleBindings getPropertyBindings = new SimpleBindings();
    /* ************************************************************ */

    public Bindings getExports() {
        return exports;
    }
    public ScriptInstance(File file) throws IOException, ScriptException
    {
        if (!file.exists())
            throw new IllegalArgumentException("file");

        this.file = file;
        compileScript();
    }

    public void dispose()
    {
        for (TimerTask task : tasks.values())
            TaskRegistry.remove(task);
        tasks.clear();

        for (ParserCommandBase command : commands)
            FECommandManager.deegisterCommand(command.getName());
        commands.clear();

        for (JsEvent<?> eventHandler : eventHandlers.values())
            eventHandler._unregister();
        eventHandlers.clear();
    }

    public void saveConfig()
    {
        String configPath = file.getPath().replace(".js", ".json");
        Object cfg = script.getEngine().get("config");

        if (cfg != null) {
            if (cfg instanceof Bindings) {
                cfg = ((Bindings) cfg).entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            }
            DataManager.save(cfg, new File(configPath));
        }
    }
    protected void compileScript() throws IOException, ScriptException
    {
        illegalFunctions.clear();
        script = null;
        String configPath = file.getPath().replace(".js", ".json");
        Map config = null;
        if (Files.exists(Paths.get(configPath)))
        {
            config = DataManager.load(HashMap.class, new File(configPath));
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), Charsets.UTF_8)))
        {
            LoggingHandler.felog.warn(config);
            // Load and compile script
            script = ModuleJScripting.getCompilable().compile(reader);

            // Initialization of environment and script
            invocable = (Invocable) script.getEngine();
            ScriptCompiler.initEngine(script.getEngine(), this);
            if (config != null)
            {
                script.getEngine().put("config", config);
            }
            script.eval();

            // Get exports
            exports = (Bindings) script.getEngine().get("exports");
        }
        catch (IOException | ScriptException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            // TODO: Maybe only catch certain exceptions like NullPointerException etc.
            e.printStackTrace();
            throw new ScriptException(e);
        }
        lastModified = file.lastModified();
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
        catch (NoSuchMethodException | ScriptException e)
        {
            illegalFunctions.add(fn);
            throw e;
        }
        catch (Exception e)
        {
            // TODO: Maybe only catch certain exceptions like NullPointerException etc.
            illegalFunctions.add(fn);
            throw new ScriptException(e);
        }
        finally
        {
            clearLastActive();
        }
    }

    public Object  tryCallGlobal(String fn, Object... args) throws ScriptException
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
        catch (ScriptException e)
        {
            illegalFunctions.add(fn);
            throw e;
        }
        catch (Exception e)
        {
            // TODO: Maybe only catch certain exceptions like NullPointerException etc.
            illegalFunctions.add(fn);
            throw new ScriptException(e);
        }
        finally
        {
            clearLastActive();
        }
    }

    public boolean hasGlobalCallFailed(String fnName)
    {
        return illegalFunctions.contains(fnName);
    }

    public Object call(Object fn, Object thiz, Object... args) throws NoSuchMethodException, ScriptException
    {
        try
        {
            setLastActive();
            return this.invocable.invokeMethod(fn, "call", ArrayUtils.add(args, 0, thiz));
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            // TODO: Maybe only catch certain exceptions like NullPointerException etc.
            throw new ScriptException(e);
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
        catch (ScriptException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            // TODO: Maybe only catch certain exceptions like NullPointerException etc.
            throw new ScriptException(e);
        }
        finally
        {
            clearLastActive();
        }
    }

    private void setLastActive()
    {
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
    /* Property access */

    @SuppressWarnings("unchecked")
    public <T> T getProperty(Object object, String property) throws ScriptException
    {
        property = "o." + property;
        getPropertyBindings.put("o", object);
        CompiledScript propertyScript = propertyScripts.get(property);
        if (propertyScript == null)
        {
            propertyScript = ScriptInstance.propertyEngine.compile(property);
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
            try
            {
                getPropertyBindings.put("o", object);
                Object eval = props.script.eval(getPropertyBindings);
                if (!(eval instanceof Bindings))
                    throw new ScriptException("Unable to access properties");
                Bindings bindings = (Bindings) eval;
                for (int i = 0; i < props.fields.size(); i++)
                    props.fields.get(i).set(instance, bindings.get(i));
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new ScriptException(e);
            }
        }
        return instance;
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
                chatError("Error in script callback: " + e.getMessage());
            }
        });
    }

    private int registerTimeout(TimerTask task)
    {
        int id = new Random().nextInt();
        while (tasks.containsKey(id))
            id = new Random().nextInt();
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

    public void registerScriptCommand(CommandJScriptCommand command)
    {
        commands.add(command);
        FECommandManager.registerCommand(command, true);
    }

    @SuppressWarnings({ "rawtypes" })
    public void registerEventHandler(String event, Object handler)
    {
        Class<? extends JsEvent> eventType = ScriptCompiler.eventTypes.get(event);
        if (eventType == null)
        {
            chatError(SCRIPT_ERROR_TEXT + "Invalid event type " + event);
            return;
        }
        try
        {
            // Constructor<? extends JsEvent> constructor = eventType.getConstructor(ScriptInstance.class, Object.class);
            // JsEvent<?> eventHandler = constructor.newInstance(this, handler);
            JsEvent<?> eventHandler = eventType.newInstance();
            eventHandler._script = this;
            eventHandler._handler = handler;
            eventHandler._eventType = event;

            // TODO: Handle reuse of one handler for multiple events!
            eventHandlers.put(handler, eventHandler);

            eventHandler._register();
        }
        catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e)
        {
            e.printStackTrace();
            chatError(SCRIPT_ERROR_TEXT + e.getMessage());
        }
    }

    public void unregisterEventHandler(Object handler)
    {
        JsEvent<?> eventHandler = eventHandlers.remove(handler);
        if (eventHandler == null)
            return;
        eventHandler._unregister();
    }

    /* ************************************************************ */
    /* Other & Utility */

    public File getFile()
    {
        return file;
    }

    public String getName()
    {
        String fileName = file.getAbsolutePath().substring(ModuleJScripting.getModuleDir().getAbsolutePath().length() + 1).replace('\\', '/');
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public List<CommandJScriptCommand> getCommands()
    {
        return commands;
    }

    public List<String> getEventHandlers()
    {
        return eventHandlers.values().stream().map(x -> x.getEventType()).collect(Collectors.toList());
    }

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
        ITextComponent msg = ChatOutputHandler.error(message);
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
