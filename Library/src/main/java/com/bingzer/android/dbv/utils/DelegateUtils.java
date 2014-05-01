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

import android.annotation.TargetApi;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.os.Build;

import com.bingzer.android.dbv.Delegate;

/**
 * Created by Ricky on 4/26/2014.
 */
public final class DelegateUtils {
    /**
     * Map action get a cursor. Based on what type of an action is, this method will
     * map the value get cursor.getXXX() where XXX is a type.
     *
     * @param delegate the action to map
     * @param cursor the target cursor
     * @param index the index in the cursor
     */
    @SuppressWarnings("unchecked")
    public static void mapDelegateFromCursor(Delegate delegate, Cursor cursor, int index){
        if(delegate.getType() == String.class) delegate.set(cursor.getString(index));
        else if(delegate.getType() == Integer.class) delegate.set(cursor.getInt(index));
        else if(delegate.getType() == Boolean.class) delegate.set(cursor.getInt(index) == 1);
        else if(delegate.getType() == Double.class) delegate.set(cursor.getDouble(index));
        else if(delegate.getType() == Long.class) delegate.set(cursor.getLong(index));
        else if(delegate.getType() == Short.class) delegate.set(cursor.getShort(index));
        else if(delegate.getType() == Float.class) delegate.set(cursor.getFloat(index));
        else if(delegate.getType() == byte[].class) delegate.set(cursor.getBlob(index));
        else if(delegate.getType() == Object.class) delegate.set(getObjectFromCursor(cursor, index));

        // TODO: Fix the exception message
        else throw new IllegalArgumentException("Unmapped");
    }

    /**
     * Try to get an 'object' get a cursor
     * @param cursor the target cursor
     * @param index the index in the cursor
     * @return an object
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected static Object getObjectFromCursor(Cursor cursor, int index){
        switch (cursor.getType(index)){
            case Cursor.FIELD_TYPE_BLOB:
                return cursor.getBlob(index);
            case Cursor.FIELD_TYPE_FLOAT:
                return cursor.getDouble(index);
            case Cursor.FIELD_TYPE_INTEGER:
                return cursor.getLong(index);
            case Cursor.FIELD_TYPE_STRING:
                return cursor.getString(index);
            default:
            case Cursor.FIELD_TYPE_NULL:
                return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private DelegateUtils() {
        // nothing
    }
}
