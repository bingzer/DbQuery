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

import com.bingzer.android.dbv.IConfig;

/**
 * Default configuration. By default the Id column will be named
 * <code>Id</code>. Id with the following scheme is not yet supported:
 * <ul>
 *     <li>{TABLE_NAME}Id</li>
 * </ul>
 * That will be supported in the future
 *
 * Created by Ricky Tobing on 7/19/13.
 */
class Config implements IConfig {
    String idNamingConvention;
    boolean appendTableName;
    boolean foreignKeySupport;
    boolean debug;
    boolean readOnly;

    Config(){
        this.idNamingConvention = "_Id";
        this.appendTableName = false;
        this.foreignKeySupport = false;
        this.debug = false;
        this.readOnly = false;
    }

    @Override
    public void setIdNamingConvention(String id) {
        idNamingConvention = id;
    }

    @Override
    public String getIdNamingConvention() {
        return idNamingConvention;
    }

    @Override
    public void setAppendTableNameForId(boolean appendTableName) {
        this.appendTableName = appendTableName;
    }

    @Override
    public boolean getAppendTableNameForId() {
        return appendTableName;
    }

    @Override
    public void setDebug(boolean on) {
        this.debug = on;
    }

    @Override
    public boolean getDebug() {
        return debug;
    }

    @Override
    public void setForeignKeySupport(boolean on) {
        this.foreignKeySupport = on;
    }

    @Override
    public boolean getForeignKeySupport() {
        return foreignKeySupport;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

}
