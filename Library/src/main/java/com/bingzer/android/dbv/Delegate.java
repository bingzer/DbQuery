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

import com.bingzer.android.dbv.contracts.PrimaryKeyIdentifier;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

/**
 * An action used to set/get variables. A delegate is
 * basically a reference to a method. Since Java does not support
 * reference to a method, we need this to map methods
 *
 * @param <T>
 */
@SuppressWarnings("ALL")
public abstract class Delegate<T> {

    private Class<?> type;

    /**
     * Construct a delegate
     * @param type the type
     */
    private Delegate(Class<?> type){
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
     * Delegate type for Id.
     * Helper class for {@link com.bingzer.android.dbv.IEntity.Mapper#mapId(Delegate)}
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
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeId extends Delegate<Long> {
        final IEntity entity;

        public TypeId(IEntity entity) {
            super(Long.class);
            this.entity = entity;
        }

        @Override
        public final Long get(){
            return entity.getId();
        }
    }

    /**
     * Delegate type for <code>String</code>
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeString extends Delegate<String> {
        public TypeString() {
            super(String.class);
        }
    }

    /**
     * Delegate type for <code>Integer</code>
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeInteger extends Delegate<Integer> {
        public TypeInteger() {
            super(Integer.class);
        }
    }

    /**
     * Delegate type for <code>Long</code>
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeLong extends Delegate<Long> {
        public TypeLong() {
            super(Long.class);
        }
    }

    /**
     * Delegate type for <code>Short</code>
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeShort extends Delegate<Short> {
        public TypeShort() {
            super(Short.class);
        }
    }

    /**
     * Delegate type for <code>Float</code>
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeFloat extends Delegate<Float> {
        public TypeFloat() {
            super(Float.class);
        }
    }

    /**
     * Delegate type for <code>Boolean</code>
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeBoolean extends Delegate<Boolean> {
        public TypeBoolean() {
            super(Boolean.class);
        }
    }

    /**
     * Delegate type for <code>Double</code>
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeDouble extends Delegate<Double> {
        public TypeDouble() {
            super(Double.class);
        }
    }

    /**
     * Delegate type for <code>byte[]</code>
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeBytes extends Delegate<byte[]> {
        public TypeBytes() {
            super(byte[].class);
        }
    }

    /**
     * Delegate type for <code>Object</code>
     * @see com.bingzer.android.dbv.IEntity.Mapper
     * @see Delegate
     */
    public static abstract class TypeObject extends Delegate<Object> {
        public TypeObject() {
            super(Object.class);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Delegate Mapper. Used to map a 'string' (column name) to its delegated
     * based on tis type
     */
    public static class Mapper extends HashMap<String, Delegate> implements IEntity.Mapper {
        private final PrimaryKeyIdentifier identifier;

        public Mapper(PrimaryKeyIdentifier identifier){
            this.identifier = identifier;
        }

        @Override
        public void mapId(Delegate<Long> delegate) {
            map(identifier.getPrimaryKeyColumn(), delegate);
        }

        @Override
        public Delegate get(String column) {
            return super.get(column);
        }

        @Override
        public void map(String column, Delegate delegate) {
            put(column, delegate);
        }

    }
}
