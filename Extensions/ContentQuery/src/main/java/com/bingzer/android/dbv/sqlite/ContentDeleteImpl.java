package com.bingzer.android.dbv.sqlite;

/**
 * Created by Ricky on 8/20/13.
 */
public class ContentDeleteImpl extends QueryImpl.DeleteImpl {

    public ContentDeleteImpl val(int value){
        this.value = value;
        return this;
    }

    public int value(){
        return value;
    }

}
