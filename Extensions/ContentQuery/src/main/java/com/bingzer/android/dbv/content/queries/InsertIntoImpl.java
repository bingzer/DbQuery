/**
 * Copyright 2013 Ricky Tobing
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
package com.bingzer.android.dbv.content.queries;

import android.content.ContentValues;
import android.net.Uri;

import com.bingzer.android.dbv.content.utils.UriUtils;
import com.bingzer.android.dbv.internal.queries.QueryImpl;
import com.bingzer.android.dbv.queries.IQuery;
import com.bingzer.android.dbv.queries.InsertInto;
import com.bingzer.android.dbv.utils.ContentValuesUtils;

/**
 * Created by Ricky on 8/20/13.
 */
public class InsertIntoImpl extends QueryImpl<Long> implements InsertInto {

    private Uri value;
    private ContentSet query;
    private String[] columnNames;

    public InsertIntoImpl(ContentSet query, String... columnNames){
        this.query = query;
        this.columnNames = columnNames;
    }

    @Override
    public IQuery<Long> val(Object... values) {
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columnNames.length; i++){
            ContentValuesUtils.mapContentValuesFromGenericObject(contentValues, columnNames[i], values[i]);
        }

        query.onContentValuesSet(this, contentValues);

        return this;
    }

    @Override
    public Long query() {
        return UriUtils.parseIdFromUri(value);
    }

    public void setUri(Uri value){
        this.value = value;
    }

    public static interface ContentSet {

        void onContentValuesSet(InsertIntoImpl query, ContentValues contentValues);

    }
}
