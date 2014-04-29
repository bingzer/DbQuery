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

package com.bingzer.android.dbv;

/**
 * Represents a collection of {@link com.bingzer.android.dbv.IEntity}.
 *
 * @see com.bingzer.android.dbv.IEntity
 * @see com.bingzer.android.dbv.queries.Select#query(IEntity)
 * @see com.bingzer.android.dbv.ITable#insert(IEntity)
 */
public interface IEntityList<T extends IEntity> extends Iterable<T>{

    /**
     * Adds an entity
     * @param entity entity to add
     * @return true if added, false otherwise
     */
    boolean add(T entity);

    /**
     * Creates a new entity
     * @return T
     */
    T newEntity();

}
