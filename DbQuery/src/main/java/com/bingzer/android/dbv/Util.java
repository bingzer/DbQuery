package com.bingzer.android.dbv;

/**
 * Created by 11856 on 7/16/13.
 */
public class Util {

    public static String[] toStringArray(Object... args){
        if(args == null) return null;

        String[] array = new String[args.length];
        for(int i = 0; i < args.length; i++){
            array[i] = args[i].toString();
        }

        return array;
    }


    public static String prepareWhereClause(String whereArgs, Object... args){
        // replace ? add args
        for(int i = 0; i < args.length; i++){
            whereArgs = whereArgs.replaceFirst("\\?", safeEscape(args[i]));
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

            if(obj instanceof  String){

                if(val.startsWith("'") && val.endsWith("'")){
                    val = val.substring(1, val.length());
                }

                val = "'" + ((String)obj).replaceAll("'", "''") + "'";
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

}
