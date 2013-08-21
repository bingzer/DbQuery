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
package com.bingzer.android.dbv.sqlite;

import android.content.ContentValues;
import android.net.Uri;

import com.bingzer.android.dbv.IQuery;

/**
 * Created by Ricky on 8/20/13.
 */
public class ContentInsertWithImpl implements IQuery.InsertWith{

    Uri value;
    ContentSet query;
    String[] columnNames;

    public ContentInsertWithImpl(ContentSet query, String... columnNames){
        this.query = query;
        this.columnNames = columnNames;
    }

    @Override
    public IQuery<Integer> val(Object... values) {
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < columnNames.length; i++){
            ContentUtil.mapContentValuesFromGenericObject(contentValues, columnNames[i], values[i]);
        }

        query.onContentValuesSet(this, contentValues);

        return this;
    }

    @Override
    public Integer query() {
        return null;
    }

    public void val(Uri value){
        this.value = value;
    }

    public static interface ContentSet extends IQuery<Integer> {

        void onContentValuesSet(ContentInsertWithImpl query, ContentValues contentValues);

    }
}
