package com.bingzer.android.dbv.sqlite;

import android.net.Uri;

/**
 * Created by Ricky on 8/20/13.
 */
public class ContentInsertImpl extends QueryImpl.InsertImpl {

    Uri uri;

    public ContentInsertImpl val(Uri value){
        this.uri = value;
        return this;
    }

    public String toString(){
        return uri.toString();
    }

}
