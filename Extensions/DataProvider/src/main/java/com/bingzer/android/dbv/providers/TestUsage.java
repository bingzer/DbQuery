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
package com.bingzer.android.dbv.providers;

import android.content.Context;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.SQLiteBuilder;

/**
 * Created by Ricky Tobing on 8/28/13.
 */
class TestUsage {

    public void test(){

    }

    static class BasicProvider extends DataProvider {

        @Override
        public IDatabase openDatabase() {
            IDatabase db = DbQuery.getDatabase("MyDb");
            db.open(1, new SQLiteBuilder() {
                @Override
                public Context getContext() {
                    return BasicProvider.this.getContext();
                }

                @Override
                public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                    // do modeling and stuffs
                }
            });

            return db;
        }

        @Override
        public String getAuthority() {
            return "com.bingzer.android.dbv.data";
        }
    }
}
