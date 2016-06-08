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
package com.bingzer.android.dbv.content;

import android.content.Context;
import android.net.Uri;

import com.bingzer.android.dbv.content.contracts.IResolver;
import com.bingzer.android.dbv.content.contracts.IStrictResolver;
import com.bingzer.android.dbv.internal.Resolver;
import com.bingzer.android.dbv.internal.StrictResolver;

/**
 * ContentQuery allows DbQuery style while
 * querying data from ContentProvider.
 *
 * For complete documentation please refer to:
 * <a href="https://github.com/bingzer/DbQuery/wiki/ContentQuery">https://github.com/bingzer/DbQuery/wiki/ContentQuery</a>
 *
 * @see com.bingzer.android.dbv.content.contracts.IResolver
 * @see com.bingzer.android.dbv.content.contracts.IStrictResolver
 */
public final class ContentQuery {

    /**
     * Creates an {@link com.bingzer.android.dbv.content.contracts.IResolver} for the specified URI
     * @param uri the uri string
     * @param context GOD-object {@link Context}
     * @return {@link com.bingzer.android.dbv.content.contracts.IResolver}
     */
    public static IResolver resolve(String uri, Context context){
        return resolve(Uri.parse(uri), context);
    }

    /**
     * Creates an {@link IResolver} for the specified URI
     * @param uri the uri object
     * @param context GOD-object {@link Context}
     * @return {@link IResolver}
     */
    public static IResolver resolve(Uri uri, Context context){
        return new Resolver(uri, context);
    }

    /**
     * Creates an {@link com.bingzer.android.dbv.content.contracts.IStrictResolver} for the specified URI
     * @param uri the uri string
     * @param context GOD-object {@link Context}
     * @return {@link com.bingzer.android.dbv.content.contracts.IStrictResolver}
     */
    public static IStrictResolver strictlyResolve(String uri, Context context){
        return strictlyResolve(Uri.parse(uri), context);
    }

    /**
     * Creates an {@link IStrictResolver} for the specified URI
     * @param uri the uri object
     * @param context GOD-object {@link Context}
     * @return {@link IStrictResolver}
     */
    public static IStrictResolver strictlyResolve(Uri uri, Context context){
        return new StrictResolver(uri, context);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    private ContentQuery(){
        // nothing
    }

}
