/**
 * Copyright 2013 Ricky Tobing
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

package com.bingzer.android.dbv.sqlite;

import android.content.Context;

import com.bingzer.android.dbv.IDatabase;

/**
 * Created by Ricky Tobing on 7/17/13.
 */
public abstract class SQLiteBuilder implements IDatabase.Builder {

    /**
     * Returns the context
     *
     * @return
     */
    public abstract Context getContext();

    /**
     * Called when database is about to create.
     * You should define all the table models here
     *
     * @param modeling
     */
    @Override
    public abstract void onModelCreate(IDatabase.Modeling modeling);

    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////

    @Override
    public void onUpgrade(IDatabase database, int oldVersion, int newVersion) {
        // do nothing
    }

    @Override
    public void onDowngrade(IDatabase database, int oldVersion, int newVersion) {
        // do nothing
    }

    @Override
    public void onError(Throwable error) {
        // re throw
        throw new Error(error);
    }
}
