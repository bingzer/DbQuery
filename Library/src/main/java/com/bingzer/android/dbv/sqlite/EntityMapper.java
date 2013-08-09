package com.bingzer.android.dbv.sqlite;

import android.content.ContentValues;
import android.database.Cursor;


import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IEntity;

import java.util.HashMap;

/**
 * Created by Ricky Tobing on 8/9/13.
 */
class EntityMapper extends HashMap<String, IEntity.Action> implements IEntity.Mapper{
    IConfig config;

    EntityMapper(IConfig config){
        this.config = config;
    }

    /**
     * Maps id
     *
     * @param action
     */
    @Override
    public void mapId(IEntity.Action<Integer> action) {
        map(config.getIdNamingConvention(), action);
    }

    @Override
    public void map(String column, IEntity.Action action) {
        put(column, action);
    }

    static void setAction(IEntity.Action action, Cursor cursor, int index){
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

    static void setAction(IEntity.Action action, ContentValues contentValues, String key){
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
}
