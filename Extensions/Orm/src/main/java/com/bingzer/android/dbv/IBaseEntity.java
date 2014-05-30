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

import android.database.Cursor;

/**
 * Represents contract for ORM Entity.
 * IBaseEntity should be able:
 * 1. Persist the changes to the database.
 * 2. Delete itself
 *
 */
public interface IBaseEntity extends IEntity {

    /**
     * Sets this Id
     */
    void setId(long id);

    /**
     * Save any changes.
     * If {@code getId()} is anything lower than or equal to 0 (Id <= 0)
     * then, an INSERTION operation will be performed.
     * Otherwise, it is an UPDATE operation.
     */
    void save();

    /**
     * Delete itself from the database.
     * If successful, {@code getId()} will be automatically set to -1.
     */
    void delete();

    /**
     * Reload data from the database. Useful if other code has make some changes
     * and you need to rehydrate the data - fresh - from the db
     */
    void load();

    /**
     * Load by Id.
     */
    void load(long id);

    /**
     * Convenient method to load data from a {@code Cursor} object
     */
    void load(Cursor cursor);

    /**
     * The table name that this entity represents
     */
    String getTableName();

    /**
     * Returns the environment.
     * @see IEnvironment
     */
    IEnvironment getEnvironment();

}
