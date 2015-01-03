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
package com.bingzer.android.dbv.internal;

import android.net.Uri;

import com.bingzer.android.dbv.content.utils.UriUtils;

/**
 * Created by Ricky on 8/20/13.
 */
class ContentInsertImpl extends com.bingzer.android.dbv.internal.InsertImpl {

    Uri uri;

    public ContentInsertImpl setUri(Uri value){
        this.uri = value;
        this.value = UriUtils.parseIdFromUri(uri);
        return this;
    }

    @Override
    public Long query(){
        return UriUtils.parseIdFromUri(uri);
    }

    @Override
    public String toString(){
        return uri.toString();
    }

}
