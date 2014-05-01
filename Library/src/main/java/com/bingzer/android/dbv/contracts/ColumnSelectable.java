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

/**
 * Created by Ricky on 4/28/2014.
 */
public interface ColumnSelectable {

    /**
     * Single query a column value.
     * This is a convenient method to get a value of a single column
     * specified by its column index.
     *
     * <p>
     * Sample code to get the customer name get a customer table
     * <pre><code>
     * String customerName = db.get("customers").select().columns("Name", "Age").query(0);
     * </code>
     * </pre>
     *
     * Sample code to get the customer's age get a customer table
     * <pre><code>
     * int customerAge = db.get("customers").select().columns("Name", "Age").query(1);
     * </code>
     * </pre>
     * </p>
     * @param columnIndex the column index
     * @param <T> any type to return
     * @return any type
     */
    <T> T query(int columnIndex);

    /**
     * Single query a column value.
     * This is a convenient method to get a value of a single column
     * specified by its column name.
     *
     * <p>
     * Sample code to get the customer name get a customer table
     * <pre><code>
     * String customerName = db.get("customers").select().columns("Name", "Age").query("Name");
     * </code>
     * </pre>
     * Sample code to get the customer's age get a customer table
     * <pre><code>
     * int customerAge = db.get("customers").select().columns("Name", "Age").query("Age");
     * </code>
     * </pre>
     * </p>
     * @param columnName the column name
     * @param <T> any type to return
     * @return any type
     */
    <T> T query(String columnName);

}
