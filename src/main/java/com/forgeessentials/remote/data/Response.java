package com.forgeessentials.remote.data;

import com.google.gson.JsonObject;

/**
 *
 */
public class Response {

    protected boolean success;

    public static class Success extends Response {

        protected JsonObject data;

        public Success(JsonObject data)
        {
            this.data = data;
        }

        public JsonObject getData()
        {
            return data;
        }

    }

    public static class Failure extends Response {

        protected String message;

        public Failure(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }

    }

}
