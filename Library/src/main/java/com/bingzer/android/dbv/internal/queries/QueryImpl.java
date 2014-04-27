package com.bingzer.android.dbv.internal.queries;

import com.bingzer.android.dbv.queries.IQuery;

public class QueryImpl<T> implements IQuery<T> {
    protected T value;

    public void setValue(T value){
        this.value = value;
    }

    @Override
    public T query(){
        return value;
    }
}
