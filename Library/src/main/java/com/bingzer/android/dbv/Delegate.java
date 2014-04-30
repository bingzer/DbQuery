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
 * An action used to set/from variables. A delegate is
 * basically a reference to a method. Since Java does not support
 * reference to a method, we need this to map methods
 *
 * <ul>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeBoolean Delegate.TypeBoolean} for <code>Boolean</code> getter/setter</li>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeBytes Delegate.TypeBytes} for <code>Boolean</code> getter/setter</li>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeDouble Delegate.TypeDouble} for <code>Double</code> getter/setter</li>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeFloat Delegate.TypeFloat} for <code>Float</code> getter/setter</li>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeId Delegate.TypeId} for <code>Primary Key</code> column</li>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeInteger Delegate.TypeInteger} for <code>Integer</code> getter/setter</li>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeLong Delegate.TypeLong} for <code>Long</code> getter/setter</li>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeObject Delegate.TypeObject} for <code>any object</code> getter/setter</li>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeShort Delegate.TypeShort} for <code>Short</code> getter/setter</li>
 *   <li>{@link com.bingzer.android.dbv.Delegate.TypeString Delegate.TypeString} for <code>String</code> getter/setter</li>
 * </ul>
 *
 * <p>
 * {@link com.bingzer.android.dbv.Delegate} is used inside the
 * {@link com.bingzer.android.dbv.IEntity#map(com.bingzer.android.dbv.IEntity.Mapper)}
 * implementations.
 * <br/>
 * Sample code:
 * <pre><code>
 * public class Person implements IEntity {
 *     ...
 *     public void map(IEntity.Mapper mapper){
 *         mapper.mapId(Delegate.TypeId(){
 *             ...
 *         }
 *
 *         mapper.map("Name", Delegate.TypeString(){
 *             ...
 *         }
 *
 *         mapper.map("Age", Delegate, TypeInteger(){
 *             ...
 *         }
 *     }
 * }
 * </code></pre>
 *
 * @version 2.0
 * @see com.bingzer.android.dbv.IEntity
 * @see com.bingzer.android.dbv.IEntity.Mapper
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
