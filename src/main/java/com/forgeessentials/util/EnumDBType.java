package com.forgeessentials.util;

import java.util.IllegalFormatException;

import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Throwables;

public enum EnumDBType
{
    H2_FILE(false, "org.h2.Driver", "jdbc:h2:file:%s;IGNORECASE=TRUE;FILE_LOCK=NO;MODE=MYSQL"), // file
    MySQL(true, "com.mysql.jdbc.Driver", "jdbc:mysql://%s:%d/%s"); // host, port, database

    /**
     * @param isRemote
     *            if the JDBC connection method should use the one with username and password or not.
     * @param driverName
     *            the qualified name of the JDBC connector class for this DB
     * @param connectString
     *            a formattable connection string. see existing ones.
     */
    EnumDBType(boolean isRemote, String driverName, String connectString)
    {
        this.isRemote = isRemote;
        driver = driverName;
        connect = connectString;
    }

    /**
     * loads the class used to load this database. Catches its own error and outputs it.
     */
    public void loadClass()
    {
        try
        {
            Class.forName(driver);
        }
        catch (ClassNotFoundException e)
        {
            LoggingHandler.felog.error("Could not load the " + this + " JDBC Driver! Does it exist in the lib directory?");
            Throwables.propagateIfPossible(e);
        }
    }

    /**
     * @param data
     *            Object array used in formatting
     * @return fully finished connection string
     */
    public String getConnectionString(Object... data) throws IllegalFormatException
    {
        String formatted = String.format(connect, data);
        return formatted;
    }

    public boolean isRemote;
    private final String driver;
    private String connect;
}
