package com.bingzer.android.dbv.content;

import android.content.ContentResolver;
import android.content.Context;

/**
 * ContentQuery allows DbQuery style while
 * querying data from ContentProvider
 *
 * Created by Ricky Tobing on 8/20/13.
 */
public final class ContentQuery {

    public static IResolver resolve(String uri, Context context){
        ContentResolver contentResolver = context.getContentResolver();
        return null;
    }
}
