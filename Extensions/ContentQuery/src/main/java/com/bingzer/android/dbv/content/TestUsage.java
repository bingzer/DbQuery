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
package com.bingzer.android.dbv.content;

import android.content.Context;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.content.contracts.IResolver;
import com.bingzer.android.dbv.content.contracts.IStrictResolver;

/**
 * This will be removed
 * Created by Ricky Tobing on 8/20/13.
 */
class TestUsage {
    String uri = "";
    Context context = null;

    void test(){

        IDatabase db = DbQuery.getDatabase("");

        IResolver resolver = ContentQuery.resolve(uri, context);

        IStrictResolver strictResolver = ContentQuery.strictlyResolve(uri, context);

    }
}
