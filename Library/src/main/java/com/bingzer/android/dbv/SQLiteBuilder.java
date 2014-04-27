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

package com.bingzer.android.dbv;

import android.content.Context;

/**
 * <code>IDatabase.Builder</code> implementations.
 * Always, always, always use this as your <code>IDatabase.Builder</code>
 *
 * @since 1.0
 * @author Ricky Tobing
 * @see IDatabase.Builder
 */
public abstract class SQLiteBuilder implements IDatabase.Builder {

    /**
     * Returns the GOD-object <code>context</code>.
     * You should return your <code>ApplicationContext</code> here
     * <code>
     * <pre>
     * ...
     * public Context getContext(){
     *     return getApplicationContext();
     * }
     * ...
     * </pre>
     * </code>
     * @return context
     */
    public abstract Context getContext();

    /**
     * Called when database is about to open.
     * You should define all the table models here
     *
     * @param database the instance of the database
     * @param modeling the modeling object used to model the database
     */
    @Override
    public abstract void onModelCreate(IDatabase database, IDatabase.Modeling modeling);

    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////


    /**
     * Called when upgrade from oldVersion to newVersion
     *
     * @param database   the database
     * @param oldVersion the old version
     * @param newVersion the new version
     * @return If true is returned, the code will continue
     * on {@link #onModelCreate(com.bingzer.android.dbv.IDatabase, com.bingzer.android.dbv.IDatabase.Modeling)}
     */
    @Override
    public boolean onUpgrade(IDatabase database, int oldVersion, int newVersion) {
        return false;
    }

    /**
     * Called when downgrading from oldVersion to newVersion
     *
     * @param database   the database
     * @param oldVersion the old version
     * @param newVersion the new version
     * @return If true is returned, the code will continue
     * on {@link #onModelCreate(com.bingzer.android.dbv.IDatabase, com.bingzer.android.dbv.IDatabase.Modeling)}
     */
    @Override
    public boolean onDowngrade(IDatabase database, int oldVersion, int newVersion) {
        return false;
    }

    /**
     * Called after everything gets called
     *
     * @param database the instance of the database
     */
    @Override
    public void onReady(IDatabase database) {
        // do nothing
    }

    /**
     * Called when any error is encountered.
     *
     * @param error the error
     */
    @Override
    public void onError(Throwable error) {
        throw new Error(error);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Convenient class to use if you don't need to model your database at all.
     * This class is really convenient if you have a pre-loaded database
     * <code>
     * <pre>
     * Context context = ...
     * IDatabase db = DbQuery.getDatabase("Test");
     *
     * db.open(version, new SQLiteBuilder.WithoutModeling(context));
     * </pre>
     * </code>
     */
    public static final class WithoutModeling extends SQLiteBuilder {

        Context context;

        /**
         * Supply the context here.
         * You should always use <code>ApplicationContext</code>
         * here whenever possible
         * @param context the context
         */
        public WithoutModeling(Context context){
            this.context = context;
        }

        @Override
        public Context getContext() {
            return context;
        }

        @Override
        public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
            // do nothing
        }
    }
}
