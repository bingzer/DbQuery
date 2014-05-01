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
package com.bingzer.android.dbv.content.utils;

import android.net.Uri;

/**
 * Created by Ricky on 8/21/13.
 */
public class UriUtils {

    public static long parseIdFromUri(Uri uri){
        String uriString = uri.toString();
        String valueString = uriString.substring(uriString.lastIndexOf("/") + 1, uriString.length());
        try{
            return Long.parseLong(valueString);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }

}
