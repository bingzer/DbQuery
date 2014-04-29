/**
 * Copyright 2014 Ricky Tobing
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
 *
 * ----------------------------------------------------------------------------
 *
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bingzer.android.dbv.utils;

/**
 * Utility methods
 */
public final class DbUtils {

    private static final String QUESTION_MARK = "\\?";
    private static final char QUESTION_MARK_CHAR = QUESTION_MARK.charAt(1);
    private static final String NULL = "null";

    /**
     * Use this method for built-in and/ pre-sanitize methods
     * provided by Android. (i.e: SQLiteDatabase.update() or insert())
     * The framework should sanitize the string.
     * <b>WARNING</b>
     * Do not use this anywhere to replace '?' with 'args'.
     * Use {@link #bindArgs(String, Object...)} instead.
     * @param args arguments
     * @return array of <code>String</code>
     */
    public static String[] toStringArray(Object... args){
        if(args == null || args.length == 0) return null;

        String[] array = new String[args.length];
        for(int i = 0; i < args.length; i++){
            if(args[i] != null) array[i] = args[i].toString();
        }

        if(array[0] == null) return null;

        return array;
    }

    /**
     * This method will sanitize 'args'. Use this method to
     * bind '?' with arguments.
     * @param clause any clause that has '?'
     * @param args arguments
     * @return properly sanitized and formatted string
     */
    public static String bindArgs(String clause, Object... args){
        if(args == null){
            return clause.replaceAll(QUESTION_MARK, NULL);
        }
        else {
            StringBuilder result = new StringBuilder();

            // replace ? with args
            for(int i = 0, counter = 0; i < clause.length(); i++){
                final char c = clause.charAt(i);
                if(c == QUESTION_MARK_CHAR && counter < args.length){
                    // replace this
                    result.append(safeEscape(args[counter++]));
                }
                else{
                    result.append(c);
                }
            }

            return result.toString();
        }
    }

    /**
     * Safe escape /sanitize an object. Object is checked to see
     * if it's a String/Integer/null/Character or custom.
     * If it's String/Character or customer, the string will be
     * sanitized by calling {@link #sqlEscapeString(String)}.
     * If it's a <code>Number</code>, it's not going to from sanitized.
     * @param obj any object (maybe null)
     * @return sanitized <code>String</code>
     */
    public static String safeEscape(Object obj){
        if(obj instanceof String) return sqlEscapeString((String)obj);
        else if(obj instanceof Number) return obj.toString();
        else if(obj == null) return NULL;
        else if(obj instanceof Character) return sqlEscapeString(obj.toString());
        else return sqlEscapeString(obj.toString());
    }

    /**
     * Joins string
     *
     * @param separator separator to use
     * @param strings strings to join
     * @return joined string
     */
    public static String join(String separator, String... strings) {
        StringBuilder builder = new StringBuilder();
        if (strings != null) {
            for (int i = 0; i < strings.length; i++) {
                if(strings[i] != null){
                    builder.append(strings[i]);
                    // append the separator if it's not the last one
                    if (i != strings.length - 1) builder.append(separator);
                }
            }
        }
        return builder.toString();
    }

    ////////////////////////////////////////////////////////////////////////
    // From DatabaseUtils.java
    // https://github.com/android/platform_frameworks_base/blob/
    //                  master/core/java/android/database/DatabaseUtils.java
    ////////////////////////////////////////////////////////////////////////

    private static void appendEscapedSQLString(StringBuilder sb, String sqlString) {
        sb.append('\'');
        if (sqlString.indexOf('\'') != -1) {
            int length = sqlString.length();
            for (int i = 0; i < length; i++) {
                char c = sqlString.charAt(i);
                if (c == '\'') {
                    sb.append('\'');
                }
                sb.append(c);
            }
        } else
            sb.append(sqlString);
        sb.append('\'');
    }

    private static String sqlEscapeString(String value) {
        StringBuilder builder = new StringBuilder();

        appendEscapedSQLString(builder, value);

        return builder.toString();
    }


    //////////////////////////////////////////////////////////////////////////////

    private DbUtils(){
        // nothing
    }
}
