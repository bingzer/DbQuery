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

import com.bingzer.android.dbv.IQuery;

/**
 * Created by Ricky Tobing on 7/17/13.
 */
public interface Insertable {

    /**
     * InsertWith content val
     * @param contents
     * @return
     */
    IQuery.Insert insert(ContentValues contents);

    /**
     * InsertWith
     * @param columns
     * @param values
     * @return
     */
    IQuery.Insert insert(String[] columns, Object[] values);

    /**
     * InsertWith
     * @param columns
     * @return
     */
    IQuery.InsertWith insert(String... columns);

}
