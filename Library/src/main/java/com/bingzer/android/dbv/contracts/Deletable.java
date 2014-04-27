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

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.queries.Delete;

import java.util.Collection;

/**
 * Created by Ricky Tobing on 7/17/13.
 */
public interface Deletable {


    /**
     * Delete by id
     * @param id the id to delete
     * @return Delete object
     */
    Delete delete(long id);

    /**
     * Bulk-remove by multiple ids
     * @param ids array of ids to delete
     * @return Delete object
     */
    Delete delete(long... ids);

    /**
     * Bulk-remove by multiple ids
     * @param ids collection of ids to delete
     * @return Delete object
     */
    Delete delete(Collection<Long> ids);

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Delete add specified condition
     * @param condition the condition
     * @return Delete object
     */
    Delete delete(String condition);

    /**
     * Delete add sepcified where clause
     * @param whereClause where clause
     * @param whereArgs arguments
     * @return Delete object
     */
    Delete delete(String whereClause, Object... whereArgs);

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Delete an entity.
     * This is equivalent of calling
     * <code>delete(entity.getId())</code>
     * @param entity entity to delete
     * @return Delete object
     */
    Delete delete(IEntity entity);

    /**
     * Bulk-delete several entities.
     * This is equivalent of calling
     * <code>delete(list-of-ids)</code>
     * @param entityList the entity list
     * @param <E> extends IEntity
     * @return Delete object
     */
    <E extends IEntity> Delete delete(IEntityList<E> entityList);

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
}
