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
package com.bingzer.android.dbv.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;

/**
 * Created by Ricky on 8/20/13.
 */
public class Utils {

    public static void mapContentValuesFromGenericObject(ContentValues contentValues, String key, Object value){
        ContentUtil.mapContentValuesFromGenericObject(contentValues, key, value);
    }


    public static void mapContentValuesFromAction(ContentValues contentValues, String key, IEntity.Action action){
        ContentUtil.mapContentValuesFromAction(contentValues, key, action);
    }

    @SuppressWarnings("unchecked")
    public static void mapActionToCursor(IEntity.Action action, Cursor cursor, int index){
        ContentUtil.mapActionToCursor(action, cursor, index);
    }


    public static void mapEntityFromCursor(EntityMapper mapper, IEntity entity, Cursor cursor){
        ContentUtil.mapEntityFromCursor(mapper, entity, cursor);
    }

    @SuppressWarnings("unchecked")
    public static <E extends IEntity> void mapEntityListFromCursor(EntityMapper mapper, IEntityList<E> entityList, Cursor cursor){
        ContentUtil.mapEntityListFromCursor(mapper, entityList, cursor);
    }
}
