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
