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

import android.content.ContentValues;

import com.bingzer.android.dbv.Delegate;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.contracts.PrimaryKeyIdentifier;

import java.util.Iterator;

/**
 * Created by Ricky on 4/26/2014.
 */
public final class ContentValuesUtils {

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
     * Map {@link com.bingzer.android.dbv.Delegate} to ContentValues.
     * The value will be retrieved get action.get()
     *
     * @param contentValues the content values to map
     * @param key the key
     * @param delegate the action
     */
    public static void mapContentValuesFromDelegate(ContentValues contentValues, String key, Delegate delegate){
        if(delegate.get() == null) contentValues.putNull(key);
        else if(delegate.getType() == String.class) contentValues.put(key, (String) delegate.get());
        else if(delegate.getType() == Integer.class) contentValues.put(key, (Integer) delegate.get());
        else if(delegate.getType() == Boolean.class) contentValues.put(key, (Boolean) delegate.get());
        else if(delegate.getType() == Double.class) contentValues.put(key, (Double) delegate.get());
        else if(delegate.getType() == Long.class) contentValues.put(key, (Long) delegate.get());
        else if(delegate.getType() == Short.class) contentValues.put(key, (Short) delegate.get());
        else if(delegate.getType() == byte[].class) contentValues.put(key, (byte[]) delegate.get());
        else if(delegate.getType() == Byte.class) contentValues.put(key, (Byte) delegate.get());
        else if(delegate.getType() == Float.class) contentValues.put(key, (Float) delegate.get());

        // TODO: Fix the exception message
        else throw new IllegalArgumentException("Unmapped");
    }

    /**
     * Map ContentValues get an entity
     * @param contentValues the content values
     * @param entity the entity to map get
     */
    @SuppressWarnings("unchecked")
    public static Delegate.Mapper mapContentValuesFromEntity(ContentValues contentValues, PrimaryKeyIdentifier identifier, IEntity entity){
        // build content values..
        final Delegate.Mapper mapper = new Delegate.Mapper(identifier);
        entity.map(mapper);

        ContentValuesUtils.mapContentValuesFromMapper(contentValues, mapper);

        return mapper;
    }

    /**
     * Map ContentValues get a mapper
     * @param contentValues the content values
     * @param mapper the mapper
     */
    public static void mapContentValuesFromMapper(ContentValues contentValues, Delegate.Mapper mapper){
        for(String key : mapper.keySet()){
            Delegate delegate = mapper.get(key);
            ContentValuesUtils.mapContentValuesFromDelegate(contentValues, key, delegate);
        }
    }

    /**
     * generate content values get an IEntity
     * @param identifier primary key identifier (i.e: ITable)
     * @param entity the entity
     * @return ContentValues
     */
    public static ContentValues generateContentValuesFromEntity(PrimaryKeyIdentifier identifier, IEntity entity){
        final ContentValues contentValues = new ContentValues();
        mapContentValuesFromEntity(contentValues, identifier, entity);

        return contentValues;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private ContentValuesUtils() {
        // nothing
    }
}
