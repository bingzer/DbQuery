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

import android.database.Cursor;

/**
 * Collection of utility methods that's cursor related
 */
public final class CursorUtils {

    /**
     * Try to return value from cursor
     * @param cursor the target cursor
     * @param columnName the column index
     * @param <T> any type
     * @return value
     */
    public static <T> T getValueFromCursor(Cursor cursor, String columnName){
        return getValueFromCursor(cursor, cursor.getColumnIndex(columnName));
    }

    /**
     * Try to return values from cursor
     * @param cursor the target cursor
     * @param columnIndex the column index
     * @param <T> any type
     * @return value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValueFromCursor(Cursor cursor, int columnIndex){
        return (T) DelegateUtils.getObjectFromCursor(cursor, columnIndex);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    private CursorUtils(){
        //
    }

}
