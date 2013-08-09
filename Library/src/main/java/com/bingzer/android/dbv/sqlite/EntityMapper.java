package com.bingzer.android.dbv.sqlite;

import android.content.ContentValues;
import android.database.Cursor;


import com.bingzer.android.dbv.IEntity;

import java.util.HashMap;

/**
 * Created by Ricky Tobing on 8/9/13.
 */
public class EntityMapper extends HashMap<String, IEntity.Action> implements IEntity.Mapper{

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
        // TODO: Fix the exception message
        else throw new IllegalArgumentException("Unmapped");
    }

    static void setAction(IEntity.Action action, ContentValues contentValues, String key){
        if(action.get() == null) contentValues.putNull(key);
        else if(action.getType() == String.class) contentValues.put(key, (String)action.get());
        else if(action.getType() == Integer.class) contentValues.put(key, (int) action.get());
        else if(action.getType() == Boolean.class) contentValues.put(key, (boolean)action.get());
        else if(action.getType() == Double.class) contentValues.put(key, (double)action.get());
        else if(action.getType() == Long.class) contentValues.put(key, (long)action.get());
        else if(action.getType() == Short.class) contentValues.put(key, (short)action.get());
        else if(action.getType() == Byte[].class) contentValues.put(key, (byte[])action.get());
        else if(action.getType() == byte.class) contentValues.put(key, (byte) action.get());
        else if(action.getType() == Float.class) contentValues.put(key, (float)action.get());
        // TODO: Fix the exception message
        else throw new IllegalArgumentException("Unmapped");
    }
}
