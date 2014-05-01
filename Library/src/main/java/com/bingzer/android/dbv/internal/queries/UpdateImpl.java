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
package com.bingzer.android.dbv.internal.queries;

import android.content.ContentValues;

import com.bingzer.android.dbv.queries.IQuery;
import com.bingzer.android.dbv.queries.Update;
import com.bingzer.android.dbv.utils.ContentValuesUtils;

/**
* Created by Ricky on 4/26/2014.
*/
public class UpdateImpl extends QueryImpl<Integer> implements Update, IQuery<Integer> {
    private ContentSet<UpdateImpl> query;
    protected ContentValues contentValues;

    public UpdateImpl(){
        this(null);
    }

    public UpdateImpl(ContentSet<UpdateImpl> query){
        this.query = query;
        this.value = 0;
    }

    @Override
    public Columns columns(final String... columns) {
        return new Columns() {
            @Override
            public IQuery<Integer> val(Object... values) {
                return UpdateImpl.this.val(columns, values);
            }
        };
    }

    @Override
    public IQuery<Integer> val(ContentValues values) {
        contentValues = values;

        return notifyContentValuesSet();
    }

    @Override
    public IQuery<Integer> val(String column, Object value) {
        contentValues = new ContentValues();
        ContentValuesUtils.mapContentValuesFromGenericObject(contentValues, column, value);

        return notifyContentValuesSet();
    }

    @Override
    public IQuery<Integer> val(String[] columnNames, Object[] values) {
        contentValues = new ContentValues();
        for(int i = 0; i < columnNames.length; i++){
            ContentValuesUtils.mapContentValuesFromGenericObject(contentValues, columnNames[i], values[i]);
        }

        return notifyContentValuesSet();
    }

    // notify so that we can execute the update
    private IQuery<Integer> notifyContentValuesSet(){
        if(query != null)
            query.onContentValuesSet(this, contentValues);
        return this;
    }
}
