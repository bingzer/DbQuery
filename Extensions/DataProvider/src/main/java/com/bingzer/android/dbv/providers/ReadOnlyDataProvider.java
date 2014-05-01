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

import android.content.ContentValues;
import android.net.Uri;

/**
 * This is a 'read-only' provider. If you implemented this,
 * the third-party will not be able to perform
 * <code>INSERT</code>,<code>UPDATE</code> and <code>DELETE</code>
 * operations. They are, however, allowed to query or perform
 * <code>SELECT</code>
 *
 */
public abstract class ReadOnlyDataProvider extends DataProvider {

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Read-only");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Read-only");
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Read-only");
    }

}
