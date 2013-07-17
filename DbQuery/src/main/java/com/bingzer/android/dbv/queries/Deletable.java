package com.bingzer.android.dbv.queries;

import com.bingzer.android.dbv.IQuery;

import java.util.Collection;

/**
 * Created by 11856 on 7/17/13.
 */
public interface Deletable {


    /**
     * Delete by id
     * @param id
     * @return
     */
    IQuery.Delete delete(int id);

    /**
     * Bulk-remove by multiple ids
     * @param ids
     * @return
     */
    IQuery.Delete delete(int... ids);

    /**
     * Bulk-remove by multiple ids
     * @param ids
     * @return
     */
    IQuery.Delete delete(Collection<Integer> ids);

    /**
     * Delete add specified condition
     * @param condition
     * @return
     */
    IQuery.Delete delete(String condition);

    /**
     * Delete add sepcified where clause
     * @param whereClause
     * @param whereArgs
     * @return
     */
    IQuery.Delete delete(String whereClause, Object... whereArgs);

    /**
     * Empty the table
     * @return
     */
    IQuery.Delete deleteAll();
}
