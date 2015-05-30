package com.forgeessentials.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 */
public class SocketStreamSplitter
{

    private final InputStream is;

    private final InputStreamReader reader;

    private final String separator;

    private final StringBuilder buffer = new StringBuilder();

    public SocketStreamSplitter(InputStream is, String separator)
    {
        this.is = is;
        this.reader = new InputStreamReader(is);
        this.separator = separator;
    }

    public String readNext() throws IOException
    {
        int separatorPos = buffer.toString().indexOf(separator);
        while (separatorPos < 0)
        {
            // First wait for data to arrive
            final int available = is.available();
            final int count = available > 0 ? available : 1;
            final char[] buf = new char[count];
            final int read = reader.read(buf, 0, count);
            buffer.append(buf);

            // Check if new data contained separator
            separatorPos = buffer.toString().indexOf(separator);
            if (separatorPos < 0 && read < 0)
                return null;
        }

        // Cut out the data that will be processed
        final String data = buffer.substring(0, separatorPos);
        buffer.delete(0, separatorPos + separator.length());
        return data;
    }

}
