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
 */package com.bingzer.android.dbv.contracts;

import android.database.Cursor;

import com.bingzer.android.dbv.queries.IEnumerable;

/**
 * Cursor enumerable.
 */
public interface CursorEnumerable {

    /**
     * Enumerate cursor.
     * The provider will call this method as long as
     * <code>cursor.moveNext()</code> is true
     * @param cursor the cursor
     */
    void query(IEnumerable<Cursor> cursor);

}
