package com.bingzer.android.dbv.queries;

import com.bingzer.android.dbv.IQuery;

/**
 * Created by Ricky Tobing on 8/15/13.
 */
public interface Groupable {

    IQuery.GroupBy groupBy(String... columns);

}
