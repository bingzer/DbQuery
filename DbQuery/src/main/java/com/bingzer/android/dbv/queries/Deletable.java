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

package com.bingzer.android.dbv.queries;

import com.bingzer.android.dbv.IQuery;

import java.util.Collection;

/**
 * Created by Ricky Tobing on 7/17/13.
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
