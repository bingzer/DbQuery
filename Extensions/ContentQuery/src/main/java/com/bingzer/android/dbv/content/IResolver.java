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

import android.net.Uri;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.queries.ContentSelectable;
import com.bingzer.android.dbv.queries.Countable;
import com.bingzer.android.dbv.queries.Deletable;
import com.bingzer.android.dbv.queries.Insertable;
import com.bingzer.android.dbv.queries.SelectIdentifiable;
import com.bingzer.android.dbv.queries.Selectable;
import com.bingzer.android.dbv.queries.Tangible;
import com.bingzer.android.dbv.queries.Updatable;

/**
 * Created by Ricky Tobing on 8/20/13.
 */
public interface IResolver extends
        ContentSelectable,
        SelectIdentifiable,
        Insertable, Updatable, Deletable,
        Tangible /*, Countable*/{

    /**
     * Returns the URI
     * @return the URI
     */
    Uri getUri();

    /**
     * Returns the default config
     * @return the config
     */
    IConfig getConfig();

    /**
     * Sets the default projections (columns) unless if otherwise
     * specified with {@link com.bingzer.android.dbv.IQuery.Select#columns(String...)}
     * in a select statement.
     * By default the default projections is
     * {@link com.bingzer.android.dbv.IConfig#getIdNamingConvention()}
     * @param columns the columns to set
     */
    void setDefaultProjections(String... columns);

    /**
     * Returns the default projections
     * @return projections
     */
    String[] getDefaultProjections();

    /**
     * Sets the default <code>Authority</code>
     * @param authority authority to set
     */
    void setDefaultAuthority(String authority);

    /**
     * Returns the <code>authority</code>
     * @return Authority
     */
    String getDefaultAuthority();
}
