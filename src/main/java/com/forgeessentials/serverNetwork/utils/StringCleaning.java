package com.forgeessentials.serverNetwork.utils;

import java.util.HashSet;
import java.util.Set;

public class StringCleaning
{
    public static final char[] PASSKEY_CHARS;

    static
    {
        // Build a character set to filter problematic characters
        Set<Character> chars = new HashSet<Character>();
        for (char c = 'a'; c <= 'z'; c++)
            chars.add(c);
        for (char c = 'A'; c <= 'Z'; c++)
            chars.add(c);
        for (char c = '0'; c <= '9'; c++)
            chars.add(c);
        chars.add('+');
        chars.add('-');
        chars.add(':');
        PASSKEY_CHARS = new char[chars.size()];
        int idx = 0;
        for (Character c : chars)
            PASSKEY_CHARS[idx++] = c;
    }
 
    public boolean containsValidCharacters(String str) {
        for (char c : str.toCharArray()) {
            boolean isValid = false;
            for (char validChar : PASSKEY_CHARS) {
                if (c == validChar) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                return false;
            }
        }
        return true;
    }
}
