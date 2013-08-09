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

package com.bingzer.android.dbv.queries;

import android.content.ContentValues;

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IQuery;

/**
 * Created by Ricky Tobing on 7/17/13.
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
     * Bulk-update columns add their val add specified condition.
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
     * Update using an {@link IEntity} object
     * @param entity the entity to update
     * @return
     */
    IQuery.Update update(IEntity entity);

    /**
     * Update using contentvalues insert specified id
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
