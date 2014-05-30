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
     * @see IEnvironment
     */
    protected BaseEntity(){
        this(Environment.getLocalEnvironment());
    }

    /**
     * Creates entity
     * @see IEnvironment
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
     * @see IEnvironment
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
    public final void save(){
        if(id <= 0) {
            onBeforeInsert();
            id = environment.getDatabase().get(getTableName()).insert(this).query();
            onAfterInsert();
        }
        else {
            onBeforeUpdate();
            environment.getDatabase().get(getTableName()).update(this);
            onAfterUpdate();
        }
    }

    /**
     * Delete itself from the database.
     * If successful, {@code getId()} will be automatically set to -1.
     */
    @Override
    public final void delete(){
        if(id > 0){
            onBeforeDelete();
            environment.getDatabase().get(getTableName()).delete(this);
            id = -1;
            onAfterDelete();
        }
    }

    /**
     * Reload data from the database. Useful if other code has make some changes
     * and you need to rehydrate the data - fresh - from the db
     */
    @Override
    public final void load(){
        load(id);
    }

    /**
     * Load by Id.
     */
    @Override
    public final void load(long id){
        onBeforeLoad();
        environment.getDatabase().get(getTableName()).select(id).query(this);
        onAfterLoad();
    }

    /**
     * Convenient method to load data from a {@code Cursor} object
     */
    @Override
    public final void load(Cursor cursor){
        onBeforeLoad();
        ITable table = environment.getDatabase().get(getTableName());
        EntityUtils.mapEntityFromCursor(table, this, cursor);
        onAfterLoad();
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
