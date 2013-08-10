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

import android.database.DatabaseUtils;

/**
 * Created by Ricky Tobing on 7/16/13.
 */
public class Util {

    public static String[] toStringArray(Object... args){
        if(args == null || args.length == 0) return null;

        String[] array = new String[args.length];
        for(int i = 0; i < args.length; i++){
            if(args[i] != null) array[i] = args[i].toString();
        }

        if(array != null && array[0] == null) return null;

        return array;
    }


    public static String prepareWhereClause(String whereArgs, Object... args){
        if(args == null && whereArgs.contains("?")){
            whereArgs = whereArgs.replaceAll("\\?", "null");
        }
        else if(args != null){
            // replace ? add args
            for(int i = 0; i < args.length; i++){
                whereArgs = whereArgs.replaceFirst("\\?", safeEscape(args[i]));
            }
        }

        return whereArgs;
    }

    public static String safeEscape(Object obj){
        String val;
        if(obj == null){
            val = "null";
        }
        else{
            val = obj.toString();

            if(obj instanceof String){
                val = sqlEscapeString(val);
            }
        }

        return val;
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

    public static void appendEscapedSQLString(StringBuilder sb, String sqlString) {
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

    /**
     * SQL-escape a string.
     */
    public static String sqlEscapeString(String value) {
        StringBuilder escaper = new StringBuilder();

        appendEscapedSQLString(escaper, value);

        return escaper.toString();
    }


}
