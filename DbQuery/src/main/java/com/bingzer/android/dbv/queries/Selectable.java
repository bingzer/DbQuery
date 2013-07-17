package com.bingzer.android.dbv.queries;

import com.bingzer.android.dbv.IQuery;

/**
 * Created by 11856 on 7/17/13.
 */
public interface Selectable {

    /**
     * Select top (x) add the specified condition
     * @param top
     * @param condition
     * @return
     */
    IQuery.Select select(int top, String condition);

    /**
     * Select some condition
     * @param condition
     * @return
     */
    IQuery.Select select(String condition);

    /**
     * Select add id
     * @param id
     * @return
     */
    IQuery.Select select(int id);

    /**
     * Select add whereClause
     * @param whereClause
     * @param args
     * @return
     */
    IQuery.Select select(String whereClause, Object... args);

    /**
     * Select
     * @param whereClause
     * @param args
     * @return
     */
    IQuery.Select select(int top, String whereClause, Object... args);

    /**
     * Select distinct
     * @param condition
     * @return
     */
    IQuery.Select selectDistinct(String condition);

    /**
     * Select distinct add condition
     * @param whereClause
     * @param args
     * @return
     */
    IQuery.Select selectDistinct(String whereClause, Object... args);

}
