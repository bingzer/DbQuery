/**
 * Copyright 2013 Ricky Tobing
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

/**
 * Extension for an IEntity
 *
 * @see IEntity
 */
public interface EntitySelectable {

    /**
     * Query and store the result to an {@link com.bingzer.android.dbv.IEntity}
     * <p>
     * Sample Code: Populate customer data whose name is John into a Customer object
     * <pre>
     * <code>Customer customer = new Customer();
     * ...
     * db.from("TableName").select("Name = ?", "John").query(customer);
     * </code>
     * </pre>
     * </p>
     * @see IEntity
     * @param entity the IEntity object
     */
    void query(IEntity entity);

    /**
     * Query and store the result to an {@link com.bingzer.android.dbv.IEntityList}
     * <p>
     * Sample Code: Populate a customer list whose country is US
     * <pre>
     * <code>CustomerList customerList = new CustomerList();
     * ...
     * db.from("TableName").select("Country = ?", "US").query(customerList);
     * </code>
     * </pre>
     * </p>
     * @see IEntityList
     * @param entityList the IEntityList object
     * @param <E> IEntity
     */
    <E extends IEntity> void query(IEntityList<E> entityList);

}
