package com.bingzer.android.dbv.queries;

import android.content.ContentValues;

import com.bingzer.android.dbv.IQuery;

/**
 * Created by 11856 on 7/17/13.
 */
public interface Insertable {


    /**
     * InsertWith content values
     * @param contents
     * @return
     */
    IQuery.Insert insert(ContentValues contents);

    /**
     * InsertWith
     * @param columns
     * @param values
     * @return
     */
    IQuery.Insert insert(String[] columns, Object[] values);

    /**
     * InsertWith
     * @param columns
     * @return
     */
    IQuery.Insert insert(String... columns);

}
