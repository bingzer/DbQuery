/**
 * Copyright 2014 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
package com.bingzer.android.dbv.internal.impl;

import android.content.ContentValues;

import com.bingzer.android.dbv.queries.IQuery;
import com.bingzer.android.dbv.internal.MappingUtil;
import com.bingzer.android.dbv.queries.InsertWith;

/**
* Created by Ricky on 4/26/2014.
*/
public class InsertWithImpl extends InsertImpl implements InsertWith {

    ContentSet<InsertWithImpl> query;
    String[] columnNames;

    public InsertWithImpl(ContentSet<InsertWithImpl> query, String... columnNames){
        this.query = query;
        this.columnNames = columnNames;
    }

    @Override
    public IQuery<Integer> val(Object... values) {
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columnNames.length; i++){
            MappingUtil.mapContentValuesFromGenericObject(contentValues, columnNames[i], values[i]);
        }

        query.onContentValuesSet(this, contentValues);

        return this;
    }
}
