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

import com.bingzer.android.dbv.queries.ISequence;

/**
 * Cursor enumerable.
 */
public interface CursorEnumerable {

    /**
     * A convenient method iterate through cursor.
     * The provider will call this method as long as
     * <code>cursor.moveNext()</code> is true. Cursor will be open and closed
     * automatically after this method returns
     * <p>
     * Sample code: Print and LOG all customers names in the customers table
     * <pre>
     * <code>db.from("Customers")
     *       .select().columns("Name")
     *       .query(new IEnumerable&lt;Cursor&gt;(){
     *           public void next(Cursor cursor){
     *               Log.i("TAG", "Name: " + cursor.getString(0));
     *           }
     *       });
     * </code>
     * </pre>
     * @param cursor the cursor
     */
    void query(ISequence<Cursor> cursor);

}
