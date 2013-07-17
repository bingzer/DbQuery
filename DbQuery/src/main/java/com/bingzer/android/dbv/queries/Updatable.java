package com.bingzer.android.dbv.queries;

import android.content.ContentValues;

import com.bingzer.android.dbv.IQuery;

/**
 * Created by 11856 on 7/17/13.
 */
public interface Updatable {


    /**
     * Update a column add its id
     * @param column
     * @param value
     * @param id
     * @return
     */
    IQuery.Update update(String column, Object value, int id);

    /**
     * Update a column add specified condition
     * @param column
     * @param value
     * @param condition
     * @return
     */
    IQuery.Update update(String column, Object value, String condition);

    /**
     * Update a column add specified condition
     * @param column
     * @param value
     * @param whereClause
     * @param whereArgs
     * @return
     */
    IQuery.Update update(String column, Object value, String whereClause, Object... whereArgs);

    /**
     * Bulk-update columns add their values add specified condition.
     * @param columns
     * @param values
     * @param condition
     * @return
     */
    IQuery.Update update(String[] columns, Object[] values, String condition);

    /**
     *
     * @param columns
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    IQuery.Update update(String[] columns, Object[] values, String whereClause, Object... whereArgs);

    /**
     * Update using the contentvalues
     * @param contents
     * @return
     */
    IQuery.Update update(ContentValues contents);

    /**
     * Update using contentvalues with specified id
     * @param contents
     * @param id
     * @return
     */
    IQuery.Update update(ContentValues contents, int id);

    /**
     * Update using the contentvalues
     * @param contents
     * @param whereClause
     * @param whereArgs
     * @return
     */
    IQuery.Update update(ContentValues contents, String whereClause, Object... whereArgs);
}
