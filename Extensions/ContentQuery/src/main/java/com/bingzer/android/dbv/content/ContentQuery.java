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
import android.database.Cursor;
import android.net.Uri;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.content.impl.Resolver;
import com.bingzer.android.dbv.queries.EntitySelectable;
import com.bingzer.android.dbv.queries.Pagination;
import com.bingzer.android.dbv.sqlite.ContentConfig;

/**
 * ContentQuery allows DbQuery style while
 * querying data from ContentProvider
 *
 * Created by Ricky Tobing on 8/20/13.
 */
public final class ContentQuery {

    static final IConfig config = new ContentConfig();

    public static IResolver resolve(String uri, Context context){
        return resolve(Uri.parse(uri), context);
    }

    public static IResolver resolve(Uri uri, Context context){
        return new Resolver(new ContentConfig(config), uri, context);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * For select statement
     * <p>
     *     Find a complete <code>Wiki</code> and documentation here:<br/>
     *     <a href="https://github.com/bingzer/DbQuery/wiki">https://github.com/bingzer/DbQuery/wiki</a>
     * </p>
     */
    public static interface Select extends IQuery<Cursor>, EntitySelectable, Pagination  {

        /**
         * Specified the column to return.
         * default or null will produce SELECT * FROM
         * @param columns column names
         * @return {@link Select}
         */
        ContentQuery.Select columns(String... columns);

        /**
         * Order by. To create multiple orderBy ASC or DESC or both,
         * this is possible
         * <code>
         * <pre>
         *   db.get("Table").select().orderBy("Id", "Name", "Price DESC");
         * </pre>
         * </code>
         * @param columns column names
         * @return {@link Select}
         */
        ContentQuery.Select.OrderBy orderBy(String... columns);

        /**
         * Order By
         */
        public static interface OrderBy extends IQuery<Cursor>, EntitySelectable, Pagination {

        }

    }

}
