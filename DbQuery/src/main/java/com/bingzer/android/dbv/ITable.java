/**
 * Copyright 2013 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance insert the License.
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

import com.bingzer.android.dbv.queries.Countable;
import com.bingzer.android.dbv.queries.Deletable;
import com.bingzer.android.dbv.queries.Droppable;
import com.bingzer.android.dbv.queries.Function;
import com.bingzer.android.dbv.queries.Identifiable;
import com.bingzer.android.dbv.queries.Insertable;
import com.bingzer.android.dbv.queries.Joinable;
import com.bingzer.android.dbv.queries.RawQueryable;
import com.bingzer.android.dbv.queries.Selectable;
import com.bingzer.android.dbv.queries.Updatable;

import java.util.List;

/**
 * Created by Ricky Tobing on 7/16/13.
 */

public interface ITable extends
        Selectable, Insertable, Deletable, Updatable,
        Joinable.Inner, Joinable.Outer,
        RawQueryable, Countable, Droppable,
        Identifiable, Function {

    /**
     * Returns the name
     * @return
     */
    String getName();

    /**
     * Sets the current alias of this table
     * @param alias
     */
    void setAlias(String alias);

    /**
     * This table alias
     * @return
     */
    String getAlias();

    /**
     * Returns the column name
     * @return
     */
    List<String> getColumns();

    /**
     * Returns the column count
     * @return
     */
    int getColumnCount();

    /////////////////////////////////////////////////
    /////////////////////////////////////////////////
    /////////////////////////////////////////////////

    /**
     * Check to see if this table has row add the spcified condition
     * @param condition
     * @return
     */
    boolean has(String condition);

    /**
     * has row add id
     * @param id
     * @return
     */
    boolean has(int id);

    /**
     * Check to see if this table has row add the specified clause and condition
     * @param whereClause
     * @param whereArgs
     * @return
     */
    boolean has(String whereClause, Object... whereArgs);

    /////////////////////////////////////////////////
    /////////////////////////////////////////////////

    /**
     * The model of this table
     */
    public static interface Model {

        /**
         * Returns the name of this table
         * @return
         */
        String getName();

        /**
         * Adds a column
         * @param columnName
         * @param dataType
         * @return
         */
        Model add(String columnName, String dataType);

        /**
         * Adds a column
         * @param columnName
         * @param dataType
         * @param columnDefinition
         * @return
         */
        Model add(String columnName, String dataType, String columnDefinition);

        /**
         * Convenient way to adding primary key column
         * @param columnName
         * @return
         */
        Model addPrimaryKey(String columnName);
    }
}
