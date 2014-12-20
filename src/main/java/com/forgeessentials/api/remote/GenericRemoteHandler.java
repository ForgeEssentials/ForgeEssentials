package com.forgeessentials.api.remote;

import com.google.gson.JsonElement;

/**
 *
 * @param <T>
 *            Type of the payload
 */
public abstract class GenericRemoteHandler<T> extends AbstractRemoteHandler {

    private final Class<T> dataClass;

    public GenericRemoteHandler(String id, Class<T> dataClass)
    {
        super(id);
        this.dataClass = dataClass;
    }

    /**
     * Get the generic data class
     */
    public Class<T> getDataClass()
    {
        return dataClass;
    }

    /**
     * Handles an abstract {@link RemoteRequest} that wraps the payload into a {@link JsonElement}
     */
    @Override
    @SuppressWarnings("unchecked")
    public RemoteResponse handle(RemoteSession session, RemoteRequest<JsonElement> request)
    {
        if (request.data == null || dataClass.equals(JsonElement.class))
            return handleData(session, (RemoteRequest) request);
        else
            return handleData(session, session.transformRemoteRequest(request, dataClass));
    }

    /**
     * Handle request with deserialized payload
     */
    protected abstract RemoteResponse handleData(RemoteSession session, RemoteRequest<T> request);

}