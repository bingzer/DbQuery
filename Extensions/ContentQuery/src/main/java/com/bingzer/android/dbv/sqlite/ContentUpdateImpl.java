package com.bingzer.android.dbv.sqlite;

/**
 * Created by Ricky on 8/20/13.
 */
public class ContentUpdateImpl extends QueryImpl.UpdateImpl {

    public ContentUpdateImpl val(int value){
        this.value = value;
        return this;
    }

    public int value(){
        return value;
    }
}
