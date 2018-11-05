package com.fuchenglei.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public abstract class StringUtils
{

    public StringUtils()
    {
    }

    public static String[] tokenizeToStringArray(String str, String delimiters)
    {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            StringTokenizer st = new StringTokenizer(str, delimiters);
            ArrayList tokens = new ArrayList();

            while (true)
            {
                String token;
                do
                {
                    if (!st.hasMoreTokens())
                    {
                        return toStringArray((Collection) tokens);
                    }

                    token = st.nextToken();
                    if (trimTokens)
                    {
                        token = token.trim();
                    }
                }
                while (ignoreEmptyTokens && token.length() <= 0);

                tokens.add(token);
            }
        }
    }

    public static String[] toStringArray(Collection<String> collection)
    {
        return collection == null ? null : (String[]) collection.toArray(new String[collection.size()]);
    }

    public static String[] commaDelimitedListToStringArray(String str)
    {
        return delimitedListToStringArray(str, ",");
    }

    public static String[] delimitedListToStringArray(String str, String delimiter)
    {
        return delimitedListToStringArray(str, delimiter, (String) null);
    }

    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete)
    {
        if (str == null)
        {
            return new String[0];
        }
        else if (delimiter == null)
        {
            return new String[]{str};
        }
        else
        {
            List<String> result = new ArrayList();
            int pos;
            if ("".equals(delimiter))
            {
                for (pos = 0; pos < str.length(); ++pos)
                {
                    result.add(deleteAny(str.substring(pos, pos + 1), charsToDelete));
                }
            }
            else
            {
                int delPos;
                for (pos = 0; (delPos = str.indexOf(delimiter, pos)) != -1; pos = delPos + delimiter.length())
                {
                    result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                }

                if (str.length() > 0 && pos <= str.length())
                {
                    result.add(deleteAny(str.substring(pos), charsToDelete));
                }
            }

            return toStringArray((Collection) result);
        }
    }

    public static String deleteAny(String inString, String charsToDelete)
    {
        if (hasLength(inString) && hasLength(charsToDelete))
        {
            StringBuilder sb = new StringBuilder(inString.length());

            for (int i = 0; i < inString.length(); ++i)
            {
                char c = inString.charAt(i);
                if (charsToDelete.indexOf(c) == -1)
                {
                    sb.append(c);
                }
            }

            return sb.toString();
        }
        else
        {
            return inString;
        }
    }

    public static boolean hasLength(CharSequence str)
    {
        return str != null && str.length() > 0;
    }

    public static String replace(String inString, String oldPattern, String newPattern)
    {
        if (hasLength(inString) && hasLength(oldPattern) && newPattern != null)
        {
            int index = inString.indexOf(oldPattern);
            if (index == -1)
            {
                return inString;
            }
            else
            {
                int capacity = inString.length();
                if (newPattern.length() > oldPattern.length())
                {
                    capacity += 16;
                }

                StringBuilder sb = new StringBuilder(capacity);
                int pos = 0;

                for (int patLen = oldPattern.length(); index >= 0; index = inString.indexOf(oldPattern, pos))
                {
                    sb.append(inString.substring(pos, index));
                    sb.append(newPattern);
                    pos = index + patLen;
                }

                sb.append(inString.substring(pos));
                return sb.toString();
            }
        }
        else
        {
            return inString;
        }
    }

}
