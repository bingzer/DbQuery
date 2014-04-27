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

import android.content.ContentValues;

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.queries.Insert;
import com.bingzer.android.dbv.queries.InsertWith;

/**
 * Created by Ricky Tobing on 7/17/13.
 */
public interface Insertable {

    /**
     * Insert content val
     * @param contents content values
     * @return Insert object
     */
    Insert insert(ContentValues contents);

    /**
     * Insert multiple columns and multiple objects
     * @param columns column names
     * @param values the values
     * @return Insert object
     */
    Insert insert(String[] columns, Object[] values);

    /**
     * InsertWith follows by <code>val(Object... values)</code>
     * to specify the values
     * @param columns column names
     * @return an InsertWith object
     */
    InsertWith insert(String... columns);

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Insert an entity.
     * @param entity entity
     * @return an Insert object
     */
    Insert insert(IEntity entity);

    /**
     * Bulk-insert an entity list. The returned value, (via Insert.query())
     * is an integer and returns the number of entity successfully inserted.
     * Ids are automatically populated inside the IEntity object
     * @param entityList the entity list to insert
     * @param <E> extends IEntity
     * @return an Insert object
     */
    <E extends IEntity> Insert insert(IEntityList<E> entityList);
}
