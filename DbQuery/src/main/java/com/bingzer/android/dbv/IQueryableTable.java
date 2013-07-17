package com.bingzer.android.dbv;


import android.database.Cursor;

import com.bingzer.android.dbv.queries.Deletable;
import com.bingzer.android.dbv.queries.Insertable;
import com.bingzer.android.dbv.queries.Selectable;
import com.bingzer.android.dbv.queries.Updatable;

/**
 * Functions available in a table
 *
 * Created by 11856 on 7/16/13.
 */
public interface IQueryableTable extends Selectable, Insertable, Deletable, Updatable {

    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////

    /**
     * Check to see if this table has row add the spcified condition
     * @param condition
     * @return
     */
    boolean hasRow(String condition);

    /**
     * has row add id
     * @param id
     * @return
     */
    boolean hasRow(int id);

    /**
     * Check to see if this table has row add the specified clause and condition
     * @param whereClause
     * @param whereArgs
     * @return
     */
    boolean hasRow(String whereClause, Object... whereArgs);

    /**
     * Returns the count of the specified condition
     * @param condition
     * @return
     */
    int count(String condition);

    /**
     * Returns the count of row from the whereClause
     * @param whereClause
     * @param whereArgs
     * @return
     */
    int count(String whereClause, Object... whereArgs);

    /**
     * Returns the total row available in this table
     * @return
     */
    int count();

    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////

    /**
     * Build raw sql
     * @param sql
     * @return
     */
    IQuery<Cursor> raw(String sql);
    /**
     * Build raw sql
     * @param sql
     * @return
     */
    IQuery<Cursor> raw(String sql, String... selectionArgs);

    /**
     * Drop sql
     * @return true if success
     */
    IQuery<Boolean> drop();

}
