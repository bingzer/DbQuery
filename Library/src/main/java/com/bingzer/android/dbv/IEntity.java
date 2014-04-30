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
 * Represents an entity. {@link com.bingzer.android.dbv.IEntity} is a runtime object
 * mapper from a record stored in the database to java objects and/ vice versa.
 * For example, if you have a table person with a simple columns
 * (Id,Name,Age and Person), using IEntity you could easily map their
 * values to your Person object. Everything is done during runtime.
 * <p>
 * Because of <b>no reflections</b> taken place, we need to do a manual labor.
 * The implementation of {@link com.bingzer.android.dbv.IEntity} needs to tell
 * {@link com.bingzer.android.dbv.IEntity.Mapper} what to map to what value so that
 * {@link IEntity} can be properly serialized during runtime.
 * An entity uses {@link com.bingzer.android.dbv.IEntity.Mapper}
 * to map column names and its value.
 * </p>
 * <p>
 * The default implementation of {@link com.bingzer.android.dbv.IEntity.Mapper} can be found
 * in {@link com.bingzer.android.dbv.Delegate.Mapper}
 * </p>
 * <p>
 * <pre><code>
 * // Person.java
 * public class Person implements IEntity {
 *     ...
 *     public void map(Mapper mapper){
 *         mapper.map("Name", new Delegate.TypeString(){
 *             public void set(String value){
 *                 setName(value);
 *             }
 *             public String get(){
 *                 return getName();
 *             }
 *         });
 *     }
 * }
 *
 * // Other.java
 * IDatabase db = ...
 * Person john = new Person();
 * db.get("Persons").select("Name = ?", "John Doe").query(person);
 *
 * assertEquals("John Doe", person.getName());
 *
 * </code></pre>
 * </p>
 *
 * @version 2.0
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
         * @param delegate the action used to set/get
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
