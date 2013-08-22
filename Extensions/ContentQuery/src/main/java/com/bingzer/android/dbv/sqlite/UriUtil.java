package com.bingzer.android.dbv.sqlite;

import android.net.Uri;

/**
 * Created by Ricky on 8/21/13.
 */
class UriUtil {

    static int parseIdFromUri(Uri uri){
        String uriString = uri.toString();
        String valueString = uriString.substring(uriString.lastIndexOf("/") + 1, uriString.length());
        try{
            return Integer.parseInt(valueString);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }

}
