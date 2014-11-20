package com.forgeessentials.commons;

import java.util.Collection;

public interface IReconstructData {
    public Object getFieldValue(String name);

    public String getUniqueKey();

    public Class<?> getType();

    public Collection<?> getAllValues();
}
