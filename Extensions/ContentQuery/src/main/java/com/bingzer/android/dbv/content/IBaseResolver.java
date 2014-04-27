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

import com.bingzer.android.dbv.contracts.Countable;
import com.bingzer.android.dbv.contracts.Deletable;
import com.bingzer.android.dbv.contracts.Insertable;
import com.bingzer.android.dbv.contracts.SelectIdentifiable;
import com.bingzer.android.dbv.contracts.Tangible;
import com.bingzer.android.dbv.contracts.Updatable;
import com.bingzer.android.dbv.internal.ContentConfig;

/**
 * Created by Ricky Tobing on 8/23/13.
 */
public interface IBaseResolver extends SelectIdentifiable, Insertable, Updatable, Deletable, Tangible, Countable {

    /**
     * Returns the URI
     * @return the URI
     */
    Uri getUri();

    /**
     * Returns the default config
     * @return the config
     */
    ContentConfig getConfig();

    String generateIdString();
}
