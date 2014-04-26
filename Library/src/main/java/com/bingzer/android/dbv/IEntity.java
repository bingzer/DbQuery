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
         * Use reflections to map
         * @param entity an entity
         */
        // void reflect(IEntity entity);

        /**
         *
         * @param column the column name
         * @param action the action used to set/get
         */
        void map(String column, Action action);

        /**
         * Maps id
         * @param action the action to map
         */
        void mapId(Action<Integer> action);

        ///////////////////////////////////////////////////////////////////

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
     * An action used to set/get variables. An action is
     * basically references to a method. Since Java does not support
     * reference to a method, we need this to map methods
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

        ///////////////////////////////////////////////////////////////////////////////////////////

        /**
         * Action type for Id.
         * Helper class for {@link Mapper#mapId(com.bingzer.android.dbv.IEntity.Action)}
         * <code>
         * <pre>
         * ...
         * mapper.mapId(new Action.TypeId(this){
         *    public void set(Integer value) {
         *      id = value;
         *    }
         * });
         * ...
         * </pre>
         * </code>
         *
         * @see Mapper
         * @see Action
         */
        public static abstract class TypeId extends Action<Integer>{
            final IEntity entity;

            public TypeId(IEntity entity) {
                super(Integer.class);
                this.entity = entity;
            }

            @Override
            public final Integer get(){
                return entity.getId();
            }
        }

        /**
         * Action type for <code>String</code>
         * @see Mapper
         * @see Action
         */
        public static abstract class TypeString extends Action<String>{
            public TypeString() {
                super(String.class);
            }
        }

        /**
         * Action type for <code>Integer</code>
         * @see Mapper
         * @see Action
         */
        public static abstract class TypeInteger extends Action<Integer>{
            public TypeInteger() {
                super(Integer.class);
            }
        }

        /**
         * Action type for <code>Long</code>
         * @see Mapper
         * @see Action
         */
        public static abstract class TypeLong extends Action<Long>{
            public TypeLong() {
                super(Long.class);
            }
        }

        /**
         * Action type for <code>Short</code>
         * @see Mapper
         * @see Action
         */
        public static abstract class TypeShort extends Action<Short>{
            public TypeShort() {
                super(Short.class);
            }
        }

        /**
         * Action type for <code>Float</code>
         * @see Mapper
         * @see Action
         */
        public static abstract class TypeFloat extends Action<Float>{
            public TypeFloat() {
                super(Float.class);
            }
        }

        /**
         * Action type for <code>Boolean</code>
         * @see Mapper
         * @see Action
         */public static abstract class TypeBoolean extends Action<Boolean>{
            public TypeBoolean() {
                super(Boolean.class);
            }
        }

        /**
         * Action type for <code>Double</code>
         * @see Mapper
         * @see Action
         */
        public static abstract class TypeDouble extends Action<Double>{
            public TypeDouble() {
                super(Double.class);
            }
        }

        /**
         * Action type for <code>byte[]</code>
         * @see Mapper
         * @see Action
         */
        public static abstract class TypeBytes extends Action<byte[]>{
            public TypeBytes() {
                super(byte[].class);
            }
        }

    }


}
