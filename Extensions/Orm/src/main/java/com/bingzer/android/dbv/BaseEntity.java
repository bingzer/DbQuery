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

import com.bingzer.android.dbv.contracts.IBaseEntity;
import com.bingzer.android.dbv.contracts.IEnvironment;
import com.bingzer.android.dbv.utils.EntityUtils;

public abstract class BaseEntity implements IBaseEntity {

    private long id = -1;
    protected final IEnvironment environment;

    //////////////////////////////////////////////////////////////////////////////////////////

    public BaseEntity(){
        this(Environment.getLocalEnvironment());
    }

    public BaseEntity(IEnvironment environment){
        this.environment = environment;
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public final void setId(long id) {
        this.id = id;
    }

    @Override
    public final long getId() {
        return id;
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public final IEnvironment getEnvironment(){
        return environment;
    }

    @Override
    public final void save(){
        if(id == -1) {
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

    @Override
    public final void delete(){
        if(id > 0){
            onBeforeDelete();
            environment.getDatabase().get(getTableName()).delete(this);
            onAfterDelete();
        }
    }

    @Override
    public final void load(){
        load(id);
    }

    @Override
    public final void load(long id){
        onBeforeLoad();
        environment.getDatabase().get(getTableName()).select(id).query(this);
        onAfterLoad();
    }

    @Override
    public final void load(Cursor cursor){
        onBeforeLoad();
        ITable table = environment.getDatabase().get(getTableName());
        EntityUtils.mapEntityFromCursor(table, this, cursor);
        onAfterLoad();
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    protected void onBeforeInsert(){
        // placeholder
    }

    protected void onAfterInsert(){
        // placeholder
    }

    protected void onBeforeUpdate(){
        // placeholder
    }

    protected void onAfterUpdate(){
        // placeholder
    }

    protected void onBeforeDelete(){
        // placeholder
    }

    protected void onAfterDelete(){
        // placeHolder
    }

    protected void onBeforeLoad(){
        // placeHolder
    }

    protected void onAfterLoad(){
        // placeHolder
    }

    protected void mapId(Mapper mapper){
        mapper.mapId(new Delegate.TypeId(this) {
            @Override
            public void set(Long value) {
                setId(value);
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void map(Mapper mapper) {
        mapId(mapper);
    }

}
