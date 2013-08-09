package com.bingzer.android.dbv.sqlite;

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

    public static void setAction(IEntity.Action action, Cursor cursor, int index){
        if(action.getType() == String.class) action.set(cursor.getString(index));
        else if(action.getType() == Integer.class) action.set(cursor.getInt(index));
        else if(action.getType() == Boolean.class) action.set(cursor.getInt(index) == 1);
        else if(action.getType() == Double.class) action.set(cursor.getDouble(index));
        else if(action.getType() == Long.class) action.set(cursor.getLong(index));
        else if(action.getType() == Short.class) action.set(cursor.getShort(index));
        else if(action.getType() == Float.class) action.set(cursor.getFloat(index));
        else throw new IllegalArgumentException("Unmapped : Cursor.getType() = " + cursor.getType(index));
    }
}
