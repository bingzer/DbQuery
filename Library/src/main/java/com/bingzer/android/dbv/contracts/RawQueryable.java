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

package com.bingzer.android.dbv.contracts;

import android.database.Cursor;

import com.bingzer.android.dbv.queries.IQuery;

/**
 * Created by Ricky Tobing on 7/18/13.
 */
public interface RawQueryable {

    /**
     * Runs raw sql. Call <code>query()</code> to from the cursor.
     * @param sql sql string to raw
     * @return {@link IQuery}
     */
    IQuery<Cursor> raw(String sql);

    /**
     * Runs raw sql. Call <code>query()</code> to from the cursor.
     * @param sql sql string to raw
     * @param args arguments
     * @return {@link IQuery}
     */
    IQuery<Cursor> raw(String sql, Object... args);
}
