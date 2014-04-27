/**
 * Copyright 2014 Ricky Tobing
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
import com.bingzer.android.dbv.queries.Update;

/**
 * Created by Ricky Tobing on 7/17/13.
 */
public interface Updatable {

    /**
     * Update using id
     * @param id the id
     * @return Update object
     */
    Update update(long id);

    /**
     * Bulk-update using ids
     * @param ids the ids
     * @return Update object
     */
    Update update(long... ids);

    /**
     * Update using a condition
     * @param condition the condition
     * @return Update object
     */
    Update update(String condition);

    /**
     * Update using a condition
     * @param whereClause whereClause
     * @param whereArgs arguments
     * @return Update object
     */
    Update update(String whereClause, Object... whereArgs);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Update using an {@link IEntity} object
     * @param entity the entity to update
     * @return Update object
     */
    Update update(IEntity entity);

    /**
     * Bulk-update using {@link IEntityList} object
     * @param entityList IEntityList object
     * @param <E> extends IEntity
     * @return Update object
     */
    <E extends IEntity> Update update(IEntityList<E> entityList);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Update using {@link ContentValues} insert specified id
     * @param contents the ContentValues
     * @param id the id
     * @return Update object
     */
    Update update(ContentValues contents, long id);

    /**
     * Update using the {@link ContentValues}
     * @param contents the ContentValues
     * @param whereClause whereClause
     * @param whereArgs arguments
     * @return Update object
     */
    Update update(ContentValues contents, String whereClause, Object... whereArgs);
}
