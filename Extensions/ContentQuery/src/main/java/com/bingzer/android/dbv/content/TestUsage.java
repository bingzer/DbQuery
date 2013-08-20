package com.bingzer.android.dbv.content;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by Ricky Tobing on 8/20/13.
 */
class TestUsage {
    String uri = "";
    Context context = null;

    void test(){

        IResolver resolver = ContentQuery.resolve(uri, context);

        Cursor cursor = resolver.select(1).query();
    }
}
