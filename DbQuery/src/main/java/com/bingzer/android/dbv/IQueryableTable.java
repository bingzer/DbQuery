/**
 * Copyright 2013 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
