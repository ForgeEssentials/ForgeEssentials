package com.forgeessentials.api.remote;

import com.forgeessentials.api.APIRegistry;
import com.google.gson.JsonElement;

/**
 *
 * @param <T> Type of the payload
 */
public abstract class GenericRemoteHandler<T> extends AbstractRemoteHandler {

    private final Class<T> dataClass;

    public GenericRemoteHandler(Class<T> dataClass, String id)
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
            return handleData(session, new RemoteRequest(request.id, request.rid, request.auth, APIRegistry.remoteManager.getGson().fromJson(request.data, dataClass)));
    }

    /**
     * Handle request with deserialized payload
     */
    protected abstract RemoteResponse handleData(RemoteSession session, RemoteRequest<T> data);

}