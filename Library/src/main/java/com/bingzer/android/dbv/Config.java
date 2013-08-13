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

package com.bingzer.android.dbv;

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
public class Config implements IConfig{
    protected String idNamingConvention;
    protected boolean appendTableName;

    public Config(){
        idNamingConvention = "Id";
        appendTableName = false;
    }

    /**
     * Sets naming convention for Id
     *
     * @param id
     */
    @Override
    public void setIdNamingConvention(String id) {
        idNamingConvention = id;
    }

    /**
     * Returns namving convention for id
     *
     * @return
     */
    @Override
    public String getIdNamingConvention() {
        return idNamingConvention;
    }

    /**
     * Sets append table name as prefix in front of the "Id".
     * The Id is defined by {@link #setIdNamingConvention(String)}.
     * The default is <code>false</code>
     *
     * @param appendTableName true to append
     */
    @Override
    public void setAppendTableNameForId(boolean appendTableName) {
        this.appendTableName = appendTableName;
    }

    /**
     * Returns true if DbQuery should always append the table name
     * in front of the Id.
     *
     * @return true if DbQuery should append table name, else if otherwise
     */
    @Override
    public boolean getAppendTableNameForId() {
        return appendTableName;
    }
}
