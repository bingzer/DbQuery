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
package com.bingzer.android.dbv.data.test;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.content.ContentQuery;
import com.bingzer.android.dbv.content.IResolver;

/**
 * Created by Ricky Tobing on 8/28/13.
 */
public class BasicDataProviderTest extends AndroidTestCase {


    public void testConnect(){
        IResolver resolver = ContentQuery.resolve("content://com.bingzer.android.dbv.data.test/Album", getContext());
        resolver.getConfig().setAppendTableNameForId(true);
        resolver.getConfig().setIdNamingConvention("Id");

        Cursor cursor = resolver.select().query();
        assertTrue(cursor.getCount() > 0);
        cursor.close();
    }


    public void testConnect2(){
        IResolver resolver = ContentQuery.resolve("content://com.bingzer.android.dbv.data.test/Album", getContext());
        resolver.getConfig().setDefaultProjections("_Id", "Title");
        resolver.getConfig().setAppendTableNameForId(true);
        resolver.getConfig().setIdNamingConvention("Id");

        Cursor cursor = resolver.select().query();
        assertTrue(cursor.getCount() > 0);
        cursor.close();
    }
}
