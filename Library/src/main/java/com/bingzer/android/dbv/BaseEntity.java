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

import com.bingzer.android.dbv.utils.EntityUtils;

/**
 * Represents the very basic of ORM entity.
 * You should use this or implement IBaseEntity yourself.
 */
public abstract class BaseEntity implements IBaseEntity {

    protected long id = -1;
    protected final IEnvironment environment;

    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates Entity with 'local' environment
     * @see com.bingzer.android.dbv.IEnvironment
     */
    protected BaseEntity(){
        this(Environment.getLocalEnvironment());
    }

    /**
     * Creates entity
     * @see com.bingzer.android.dbv.IEnvironment
     * @param environment the environment that this entity should live
     */
    protected BaseEntity(IEnvironment environment){
        this.environment = environment;
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets this Id
     */
    @Override
    public final void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the id
     * @return the id
     */
    @Override
    public final long getId() {
        return id;
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the environment.
     * @see com.bingzer.android.dbv.IEnvironment
     */
    @Override
    public final IEnvironment getEnvironment(){
        return environment;
    }

    /**
     * Save any changes.
     * If {@code getId()} is anything lower than or equal to 0 (Id <= 0)
     * then, an INSERTION operation will be performed.
     * Otherwise, it is an UPDATE operation.
     */
    @Override
    public final boolean save(){
        if(id <= 0) {
            onBeforeInsert();
            id = environment.getDatabase().get(getTableName()).insert(this).query();
            onAfterInsert();
            return id > 0;
        }
        else {
            onBeforeUpdate();
            int numUpdated = environment.getDatabase().get(getTableName()).update(this).query();
            onAfterUpdate();
            return numUpdated == 1;
        }
    }

    /**
     * Delete itself from the database.
     * If successful, {@code getId()} will be automatically set to -1.
     */
    @Override
    public final boolean delete(){
        if(id > 0){
            onBeforeDelete();
            int numDeleted = environment.getDatabase().get(getTableName()).delete(this).query();
            id = -1;
            onAfterDelete();

            return numDeleted == 1;
        }

        return false;
    }

    /**
     * Reload data from the database. Useful if other code has make some changes
     * and you need to rehydrate the data - fresh - from the db
     */
    @Override
    public final boolean load(){
        return load(id);
    }

    /**
     * Load by Id.
     */
    @Override
    public final boolean load(long id){
        onBeforeLoad();
        environment.getDatabase().get(getTableName()).select(id).query(this);
        onAfterLoad();
        return this.id == id;
    }

    /**
     * Convenient method to load data from a {@code Cursor} object
     */
    @Override
    public final boolean load(Cursor cursor){
        if(cursor != null && cursor.getCount() > 0){
            onBeforeLoad();
            ITable table = environment.getDatabase().get(getTableName());
            EntityUtils.mapEntityFromCursor(table, this, cursor);
            onAfterLoad();
            return true;
        }

        return false;
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoked before INSERT operation is performed
     */
    protected void onBeforeInsert(){
        // placeholder
    }

    /**
     * Invoked after INSERT operation is performed
     */
    protected void onAfterInsert(){
        // placeholder
    }

    /**
     * Invoked before UPDATE operation is performed
     */
    protected void onBeforeUpdate(){
        // placeholder
    }

    /**
     * Invoked after UPDATE operation is performed
     */
    protected void onAfterUpdate(){
        // placeholder
    }

    /**
     * Invoked before DELETE operation is performed
     */
    protected void onBeforeDelete(){
        // placeholder
    }

    /**
     * Invoked after DELETE operation is performed
     */
    protected void onAfterDelete(){
        // placeHolder
    }

    /**
     * Invoked before LOAD operation is performed
     */
    protected void onBeforeLoad(){
        // placeHolder
    }

    /**
     * Invoked after LOAD operation is performed
     */
    protected void onAfterLoad(){
        // placeHolder
    }

    /**
     * Convenient method to map the {@code Id}.
     * Use this when implementing {@link #map(com.bingzer.android.dbv.IEntity.Mapper)}
     * to map the Id
     */
    protected void mapId(Mapper mapper){
        mapper.mapId(new Delegate.TypeId(this) {
            @Override
            public void set(Long value) {
                setId(value);
            }
        });
    }

}
