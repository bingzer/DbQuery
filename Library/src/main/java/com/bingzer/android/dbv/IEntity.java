/**
 * Copyright 2014 Ricky Tobing
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

/**
 * Represents an entity. {@link IEntity} can be serialized.
 * An entity uses {@link com.bingzer.android.dbv.IEntity.Mapper}
 * to map column names and its value.
 *
 * The default implementation of this mapper can be found
 * in {@link com.bingzer.android.dbv.Delegate.Mapper}
 *
 * @see com.bingzer.android.dbv.IEntityList
 * @see com.bingzer.android.dbv.queries.Select#query(IEntity)
 * @see com.bingzer.android.dbv.ITable#insert(IEntity)
 */
public interface IEntity {

    /**
     * Returns the id
     * @return the id
     */
    long getId();

    /**
     * Determines how to map column and the class variable.
     * @param mapper the mapper object
     */
    void map(Mapper mapper);

    ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The Mapper object.
     * This object is used to map column names and variable
     */
    public static interface Mapper {

        /**
         *
         * @param column the column name
         * @param delegate the action used to set/from
         */
        void map(String column, Delegate delegate);

        /**
         * Maps id
         * @param delegate the action to map
         */
        void mapId(Delegate<Long> delegate);

        ///////////////////////////////////////////////////////////////////

        /**
         * Clears all the mapping
         */
        void clear();

        /**
         * Returns the <code>Action</code> object
         * for the specified column.
         * Null if not found
         * @see com.bingzer.android.dbv.Delegate
         * @param column column name
         * @return <code>Action</code>
         */
        Delegate get(String column);
    }
}
