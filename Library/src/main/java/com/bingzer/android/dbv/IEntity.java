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

package com.bingzer.android.dbv;

import java.lang.reflect.ParameterizedType;

/**
 * Represents an entity. {@link IEntity} can be serialized.
 *
 * Created by Ricky Tobing on 8/9/13.
 *
 * @see com.bingzer.android.dbv.IQuery.Select#query(IEntity)
 * @see com.bingzer.android.dbv.ITable#insert(IEntity)
 */
public interface IEntity {

    /**
     * Returns the id
     * @return the id
     */
    int getId();

    /**
     * Determines how to map column and the class variable.
     * @param mapper the mapper object
     */
    void map(Mapper mapper);


    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * The Mapper object.
     * This object is used to map column names and variable
     */
    public static interface Mapper {

        /**
         *
         * @param column the column name
         * @param action the action used to set/get
         */
        void map(String column, Action action);

        /**
         * Maps id
         * @param action
         */
        void mapId(Action<Integer> action);

        /**
         * Clears all the mapping
         */
        void clear();

        /**
         * Returns the <code>Action</code> object
         * for the specified column.
         * Null if not found
         * @see Action
         * @param column column name
         * @return <code>Action</code>
         */
        Action get(String column);
    }

    /**
     * An action used to set/get variables
     *
     * @param <T>
     */
    public static abstract class Action<T>{

        private Class<?> type;

        /**
         *
         * @param type
         */
        public Action(Class<?> type){
            if(type == null) {
                // use reflection
                try{
                    type = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                }
                catch (ClassCastException e){
                    type = Object.class;
                }
            }

            this.type = type;
        }

        public Class<?> getType(){
            return type;
        }

        /**
         * Sets the value
         * @param value the value to set
         */
        public abstract void set(T value);

        /**
         * Returns the value
         * @return the value
         */
        public abstract T get();

    }


}
