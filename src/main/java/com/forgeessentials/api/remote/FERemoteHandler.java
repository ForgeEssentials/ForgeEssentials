package com.forgeessentials.api.remote;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FERemoteHandler {

	String id();

}
