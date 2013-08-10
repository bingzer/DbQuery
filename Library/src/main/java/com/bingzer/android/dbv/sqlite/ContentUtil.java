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
package com.bingzer.android.dbv.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.bingzer.android.dbv.IEntity;

/**
 * Created by Ricky on 8/9/13.
 */
class ContentUtil {

    static void mapContentValuesFromGenericObject(ContentValues contentValues, String key, Object value){
        if(value == null) contentValues.putNull(key);
        else if(value instanceof Boolean) contentValues.put(key, (Boolean) value);
        else if(value instanceof Byte) contentValues.put(key, (Byte) value);
        else if(value instanceof byte[]) contentValues.put(key, (byte[]) value);
        else if(value instanceof Double) contentValues.put(key, (Double) value);
        else if(value instanceof Float) contentValues.put(key, (Float) value);
        else if(value instanceof Integer) contentValues.put(key, (Integer) value);
        else if(value instanceof Long) contentValues.put(key, (Long) value);
        else if(value instanceof Short) contentValues.put(key, (Short) value);
        else if(value instanceof String) contentValues.put(key, (String) value);
        // todo: fix exception message
        else throw new IllegalArgumentException("Unmapped");
    }


    static void mapContentValuesFromAction(ContentValues contentValues, String key, IEntity.Action action){
        if(action.get() == null) contentValues.putNull(key);
        else if(action.getType() == String.class) contentValues.put(key, (String)action.get());
        else if(action.getType() == Integer.class) contentValues.put(key, (Integer) action.get());
        else if(action.getType() == Boolean.class) contentValues.put(key, (Boolean)action.get());
        else if(action.getType() == Double.class) contentValues.put(key, (Double)action.get());
        else if(action.getType() == Long.class) contentValues.put(key, (Long)action.get());
        else if(action.getType() == Short.class) contentValues.put(key, (Short)action.get());
        else if(action.getType() == byte[].class) contentValues.put(key, (byte[])action.get());
        else if(action.getType() == Byte.class) contentValues.put(key, (Byte) action.get());
        else if(action.getType() == Float.class) contentValues.put(key, (Float)action.get());
            // TODO: Fix the exception message
        else throw new IllegalArgumentException("Unmapped");
    }

    static void mapActionToCursor(IEntity.Action action, Cursor cursor, int index){
        if(action.getType() == String.class) action.set(cursor.getString(index));
        else if(action.getType() == Integer.class) action.set(cursor.getInt(index));
        else if(action.getType() == Boolean.class) action.set(cursor.getInt(index) == 1);
        else if(action.getType() == Double.class) action.set(cursor.getDouble(index));
        else if(action.getType() == Long.class) action.set(cursor.getLong(index));
        else if(action.getType() == Short.class) action.set(cursor.getShort(index));
        else if(action.getType() == Float.class) action.set(cursor.getFloat(index));
        else if(action.getType() == byte[].class) action.set(cursor.getBlob(index));

            // TODO: Fix the exception message
        else throw new IllegalArgumentException("Unmapped");
    }
}
