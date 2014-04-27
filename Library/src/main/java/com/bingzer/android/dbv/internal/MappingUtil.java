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
package com.bingzer.android.dbv.internal;

import android.content.ContentValues;
import android.database.Cursor;

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.ITable;

/**
 * Provides a 'mapping' utility methods.
 * Created by Ricky on 8/9/13.
 */
public class MappingUtil {

    /**
     * Map key + value to ContentValues.
     * Convenient method to map generic object to a ContentValues.
     *
     * @param contentValues content values to map these key + value
     * @param key the key
     * @param value value.
     */
    public static void mapContentValuesFromGenericObject(ContentValues contentValues, String key, Object value){
        if(value instanceof String) contentValues.put(key, (String) value);
        else if(value instanceof Integer) contentValues.put(key, (Integer) value);
        else if(value == null) contentValues.putNull(key);
        else if(value instanceof Boolean) contentValues.put(key, (Boolean) value);
        else if(value instanceof Byte) contentValues.put(key, (Byte) value);
        else if(value instanceof byte[]) contentValues.put(key, (byte[]) value);
        else if(value instanceof Double) contentValues.put(key, (Double) value);
        else if(value instanceof Float) contentValues.put(key, (Float) value);
        else if(value instanceof Long) contentValues.put(key, (Long) value);
        else if(value instanceof Short) contentValues.put(key, (Short) value);

        // todo: fix exception message
        else throw new IllegalArgumentException("Unmapped");
    }

    /**
     * Map {@link com.bingzer.android.dbv.IEntity.Action} to ContentValues.
     * The value will be retrieved from action.get()
     *
     * @param contentValues the content values to map
     * @param key the key
     * @param action the action
     */
    public static void mapContentValuesFromAction(ContentValues contentValues, String key, IEntity.Action action){
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

    /**
     * Map action from a cursor. Based on what type of an action is, this method will
     * map the value from cursor.getXXX() where XXX is a type.
     *
     * @param action the action to map
     * @param cursor the target cursor
     * @param index the index in the cursor
     */
    @SuppressWarnings("unchecked")
    public static void mapActionFromCursor(IEntity.Action action, Cursor cursor, int index){
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

    /**
     * Maps an entity from a cursor. Cursor will be automatically close
     * @param table the table
     * @param entity the entity to map
     * @param cursor the cursor
     */
    public static void mapEntityFromCursor(ITable table, IEntity entity, Cursor cursor){
        EntityMapper mapper = new EntityMapper((Table) table);
        entity.map(mapper);
        if(cursor.moveToNext()){
            for(int i = 0; i < cursor.getColumnCount(); i++){
                String columnName = cursor.getColumnName(i);
                IEntity.Action action = mapper.get(columnName);
                if(action != null){
                    MappingUtil.mapActionFromCursor(action, cursor, i);
                }
            }
        }

        cursor.close();
    }

    /**
     * Maps an entity list from cursor. Cursor will be automatically close
     * @param table the table
     * @param entityList the list to map
     * @param cursor the cursor
     * @param <E> type of IEntity
     */
    @SuppressWarnings("unchecked")
    public static <E extends IEntity> void mapEntityListFromCursor(ITable table, IEntityList<E> entityList, Cursor cursor){
        EntityMapper mapper = new EntityMapper((Table) table);
        while(cursor.moveToNext()){
            int columnIdIndex = cursor.getColumnIndex(mapper.table.generateIdString());
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
                IEntity.Action action = mapper.get(columnName);
                if(action != null){
                    MappingUtil.mapActionFromCursor(action, cursor, i);
                }
            }
        }// end while

        cursor.close();
    }


}
