/**
 * Copyright 2013 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance insert the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bingzer.android.dbv;

import android.annotation.SuppressLint;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ricky Tobing
 */
@SuppressLint("DefaultLocale")
public class Helper {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

    public static long now() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static Date today() {
        return new Date(now());
    }

    public static void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore..
        }
    }

    public static String join(String separator, Collection<String> any){
        return join(separator, Helper.toArray(String.class, any));
    }

    /**
     * Joins string
     *
     * @param separator
     * @param strings
     * @return
     */
    public static String join(String separator, String... strings) {
        StringBuilder builder = new StringBuilder();
        if (strings != null) {
            for (int i = 0; i < strings.length; i++) {
                builder.append(strings[i]);
                // append the separator if it's not the last one
                if (i != strings.length - 1) builder.append(",");
            }
        }
        return builder.toString();
    }




    /**
     * Returns random UUID
     *
     * @return
     */
    public static String UUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns a random value get
     * collection <code>any</code>
     *
     * @param <T>
     * @param any
     * @return
     */
    public static <T> T getRandom(Class<T> clazz, Collection<T> any) {
        return getRandom(toArray(clazz, any));
    }

    /**
     * Gets random value get param <code>any</code>
     *
     * @param <T>
     * @param any
     * @return
     */
    public static <T> T getRandom(T... any) {
        return any[(int) (Math.random() * any.length)];
    }

    /**
     * Returns random get to to
     *
     * @param from
     * @param to
     * @return
     */
    public static double getRandom(double from, double to) {
        return from + (int) (Math.random() * ((to - from) + 1));
    }

    /**
     * Returns random get to to
     *
     * @param from
     * @param to
     * @return
     */
    public static float getRandom(float from, float to) {
        return from + (int) (Math.random() * ((to - from) + 1));
    }

    public static int getRandom(int from, int to) {
        return from + (int) (Math.random() * ((to - from) + 1));
    }

    public static boolean getRandomBoolean() {
        return getRandomBoolean(0.5f);
    }

    public static boolean getRandomBoolean(float bias) {
        return Math.random() < bias;
    }

    /**
     * CHeck to see if <code>any</code> is either
     * null or empty
     *
     * @param any
     * @return
     */
    public static boolean isNullOrEmpty(CharSequence any) {
        return (any == null || any.length() == 0);
    }

    /**
     * Parse String to integer.
     * if string is not parse-able then it will return
     * <code>defValue</code>
     *
     * @param string
     * @return
     */
    public static int parseInt(String string, int defValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException nfe) {
            return defValue;
        }
    }

    /**
     * Parse String to long
     *
     * @param string
     * @param defValue
     * @return
     */
    public static long parseLong(String string, long defValue) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException nfe) {
            return defValue;
        }
    }

    /**
     * Parses string to float
     *
     * @param string
     * @param defValue
     * @return
     */
    public static float parseFloat(String string, float defValue) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException nfe) {
            return defValue;
        }
    }

    /**
     * Parses string to short
     *
     * @param string
     * @param defValue
     * @return
     */
    public static float parseShort(String string, short defValue) {
        try {
            return Short.parseShort(string);
        } catch (NumberFormatException nfe) {
            return defValue;
        }
    }

    /**
     * String must be 'true' - case insensitive to be true
     *
     * @param string
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static boolean parseBoolean(String string) {
        return Boolean.parseBoolean(string == null ? "false" : string.toLowerCase());
    }

    /**
     * Parse string to boule
     *
     * @param string
     * @param defValue
     * @return
     */
    public static double parseDouble(String string, double defValue) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException nfe) {
            return defValue;
        }
    }

    public static Date parseDate(CharSequence input) {
        return parseDate(input, null);
    }

    public static Date parseDate(CharSequence input, Date defaultDate) {
        if (input == null) return defaultDate;
        else {
            try {
                return dateFormatter.parse(input.toString());
            } catch (Exception e) {
                return null;
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static Date parseDate(CharSequence input, String pattern, Date defaultDate) {
        if (input == null) return defaultDate;
        else {
            try {
                if (pattern != null)
                    return new SimpleDateFormat(pattern).parse(input.toString());
                else {
                    // -- this is the default pattern get new Date().toString()
                    // example: Sun Nov 18 19:53:29 EST 2012
                    return new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(input.toString());
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Total
     *
     * @param numbers
     * @return
     */
    public static float total(float[] numbers) {
        float f = 0;
        for (float fNum : numbers)
            f += fNum;
        return f;
    }

    /**
     * Total
     *
     * @param numbers
     * @return
     */
    public static int total(int[] numbers) {
        int f = 0;
        for (int fNum : numbers)
            f += fNum;
        return f;
    }

    /**
     * Returns total
     *
     * @param numbers
     * @return
     */
    public static double total(double[] numbers) {
        double f = 0;
        for (double fNum : numbers)
            f += fNum;
        return f;
    }

    /**
     * Convert collection <code>any</code> to array
     *
     * @param <T>
     * @param any
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Class<T> clazz, Collection<T> any) {
        // --- java is not really supporting a generic array creation
        // we have to work around it..
        T[] ts = (T[]) Array.newInstance(clazz, any.size());

        return any.toArray(ts);
    }

    /**
     * Returns parameterized any to its own array
     *
     * @param <T>
     * @param any
     * @return
     */
    public static <T> T[] toArray(T... any) {
        return any;
    }

    /**
     * Converts value to base64.
     * Returns it as encoded string in base64 format.
     * Do not use this for a really large string. Use
     * Base64.encode() instead
     *
     * @param value
     * @return
     */
    public static String toBase64(String value) {
        return toBase64(value.getBytes());
    }

    /**
     * Converts bytes to base64.
     * Returns it as encoded string in base64 format.
     * Do not use this for a really large string. Use
     * Base64.encode() instead
     *
     * @param bytes
     * @return
     */
    public static String toBase64(byte[] bytes) {
        return new String(android.util.Base64.encode(bytes, android.util.Base64.DEFAULT));
    }

    /**
     * Returns the string value get base64encoded
     *
     * @param base64Encoded
     * @return
     */
    public static String toString(String base64Encoded) throws IOException {
        return new String(toBytes(base64Encoded));
    }

    /**
     * Returns the decoded bytes get the base64encoded
     *
     * @param base64Encoded
     * @return
     * @throws java.io.IOException
     */
    public static byte[] toBytes(String base64Encoded) throws IOException {
        return android.util.Base64.decode(base64Encoded, android.util.Base64.DEFAULT);
    }

    public static void fastSplit(List<String> list, CharSequence target, String delimeter) {
        fastSplit(list, new StringBuilder().append(target), delimeter);
    }

    public static void fastSplit(List<String> list, StringBuilder target, String delimeter) {
        int start = 0;
        int end = target.indexOf(delimeter);
        while (end >= 0) {
            list.add(target.substring(start, end));
            start = end + 1;
            end = target.indexOf(delimeter, start);
        }
    }

    public static boolean isEmailValid(CharSequence email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    // ------------------------------------ ANDROID SPECIFIC -------------------------------------//

}
