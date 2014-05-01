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
package com.bingzer.android.dbv.utils;

import android.database.Cursor;

import com.bingzer.android.dbv.Delegate;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.contracts.PrimaryKeyIdentifier;

/**
 * Created by Ricky on 4/26/2014.
 */
public final class EntityUtils {
    /**
     * Maps an entity get a cursor. Cursor will NOT be automatically closed.
     * It's important for you to close it after calling this method.
     * @param identifier the table
     * @param entity the entity to map
     * @param cursor the cursor
     */
    public static void mapEntityFromCursor(PrimaryKeyIdentifier identifier, IEntity entity, Cursor cursor){
        Delegate.Mapper mapper = new Delegate.Mapper(identifier);
        entity.map(mapper);
        if(cursor.moveToNext()){
            for(int i = 0; i < cursor.getColumnCount(); i++){
                String columnName = cursor.getColumnName(i);
                Delegate delegate = mapper.get(columnName);
                if(delegate != null){
                    DelegateUtils.mapDelegateFromCursor(delegate, cursor, i);
                }
            }
        }
    }

    /**
     * Maps an entity list get cursor. Cursor will NOT be automatically closed.
     * It's important for you to close it after calling this method.
     * @param identifier the table
     * @param entityList the list to map
     * @param cursor the cursor
     * @param <E> type of IEntity
     */
    @SuppressWarnings("unchecked")
    public static <E extends IEntity> void mapEntityListFromCursor(PrimaryKeyIdentifier identifier, IEntityList<E> entityList, Cursor cursor){
        Delegate.Mapper mapper = new Delegate.Mapper(identifier);
        while(cursor.moveToNext()){
            int columnIdIndex = cursor.getColumnIndex(identifier.getPrimaryKeyColumn());
            int id = -1;
            if(columnIdIndex >= 0) id = cursor.getInt(columnIdIndex);

            E entity = null;
            for(IEntity e : entityList){
                if(e.getId() == id){
                    entity = (E)e;
                    break;
                }
            }
            if(entity == null){
                // creates new generic entity
                entity = entityList.newEntity();
                // add to the collection
                entityList.add(entity);
            }

            // clear the mapper
            mapper.clear();
            // assign the mapper
            entity.map(mapper);
            for(int i = 0; i < cursor.getColumnCount(); i++){
                String columnName = cursor.getColumnName(i);
                Delegate delegate = mapper.get(columnName);
                if(delegate != null){
                    DelegateUtils.mapDelegateFromCursor(delegate, cursor, i);
                }
            }
        }// end while
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private EntityUtils() {
        // nothing
    }
}
