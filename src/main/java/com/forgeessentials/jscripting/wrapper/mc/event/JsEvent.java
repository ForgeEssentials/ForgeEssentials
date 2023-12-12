package com.forgeessentials.jscripting.wrapper.mc.event;

import javax.script.ScriptException;

import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.jscripting.ScriptInstance;

import net.minecraft.command.CommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.loading.FMLEnvironment;

public abstract class JsEvent<T extends Event>
{

    /**
     * @tsd.ignore
     */
    protected T _event;

    /**
     * @tsd.ignore
     */
    public ScriptInstance _script;

    /**
     * @tsd.ignore
     */
    public Object _handler;

    /**
     * @tsd.ignore
     */
    public String _eventType;

    public T _getEvent()
    {
        return _event;
    }

    public CommandSource _getSender()
    {
        return null;
    }

    public String getEventType()
    {
        return _eventType;
    }

    public void _register()
    {
        MinecraftForge.EVENT_BUS.register(this);
        //APIRegistry.FE_EVENTBUS.register(this);
    }

    public void _unregister()
    {
        MinecraftForge.EVENT_BUS.unregister(this);
        //APIRegistry.FE_EVENTBUS.unregister(this);
    }

    protected void _callEvent(T event)
    {
        if (FMLEnvironment.dist.isClient())
            return;
        try
        {
            this._event = event;
            CommandSource sender = _getSender();
            if (sender != null)
                _script.setLastSender(sender);

            _script.call(_handler, _handler, this);
        }
        catch (NoSuchMethodException e)
        {
            // TODO: Unregister event because it's broken!
            e.printStackTrace();
            _script.chatError(ScriptInstance.SCRIPT_ERROR_TEXT + e.getMessage());
            _script.unregisterEventHandler(this);
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
            _script.chatError(ScriptInstance.SCRIPT_ERROR_TEXT + e.getMessage());
        } catch (FECommandParsingException e) {
			e.printStackTrace();
			_script.chatError(ScriptInstance.SCRIPT_ERROR_TEXT + e.error);
		}
    }

    public boolean isCancelable()
    {
        return _event.isCancelable();
    }

    public boolean isCanceled()
    {
        return _event.isCanceled();
    }

    public void setCanceled(boolean cancel)
    {
        _event.setCanceled(cancel);
    }

    public boolean hasResult()
    {
        return _event.hasResult();
    }

    public Result getResult()
    {
        return _event.getResult();
    }

    public void setResult(Result value)
    {
        _event.setResult(value);
    }

    public EventPriority getPhase()
    {
        return _event.getPhase();
    }

    public void setPhase(EventPriority value)
    {
        _event.setPhase(value);
    }

    @Override
    public String toString()
    {
        return _event == null ? "null" : _event.toString();
    }

}
