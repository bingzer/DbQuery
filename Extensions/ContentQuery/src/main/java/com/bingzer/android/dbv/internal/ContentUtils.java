/**
 * Copyright 2013 Ricky Tobing
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
package com.bingzer.android.dbv.internal;

import android.content.ContentValues;
import android.database.Cursor;

import com.bingzer.android.dbv.Delegate;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.utils.ContentValuesUtils;
import com.bingzer.android.dbv.utils.DelegateUtils;

/**
 * Created by Ricky on 8/20/13.
 */
public class ContentUtils {

    public static void mapContentValuesFromGenericObject(ContentValues contentValues, String key, Object value){
        ContentValuesUtils.mapContentValuesFromGenericObject(contentValues, key, value);
    }


    public static void mapContentValuesFromAction(ContentValues contentValues, String key, Delegate delegate){
        ContentValuesUtils.mapContentValuesFromAction(contentValues, key, delegate);
    }

    @SuppressWarnings("unchecked")
    public static void mapActionToCursor(Delegate delegate, Cursor cursor, int index){
        DelegateUtils.mapDelegateFromCursor(delegate, cursor, index);
    }

    public static void mapEntityFromCursor(IEntity.Mapper mapper, IEntity entity, Cursor cursor){
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

    @SuppressWarnings("unchecked")
    public static <E extends IEntity> void mapEntityListFromCursor(IEntity.Mapper mapper, IEntityList<E> entityList, Cursor cursor, String columnId){
        while(cursor.moveToNext()){
            int columnIdIndex = cursor.getColumnIndex(columnId);
            int id = -1;
            if(columnIdIndex >= 0) id = cursor.getInt(columnIdIndex);

            E entity = null;
            for(IEntity e : entityList.getEntityList()){
                if(e.getId() == id){
                    entity = (E)e;
                    break;
                }
            }
            if(entity == null){
                // creates new generic entity
                entity = entityList.newEntity();
                // add to the collection
                entityList.getEntityList().add(entity);
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

}
