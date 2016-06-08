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
package com.bingzer.android.dbv.internal;

import com.bingzer.android.dbv.content.contracts.IContentQueryConfig;

/**
 * Created by Ricky on 8/22/13.
 */
class ContentConfig extends Config implements IContentQueryConfig{

    private String[] defaultProjections;
    private String authority;

    public ContentConfig(){
        setIdNamingConvention("_id");
        setDefaultProjections();
    }

    /**
     * Sets the default projections (columns) unless if otherwise
     * specified with {@link com.bingzer.android.dbv.queries.Select#columns(String...)}
     * in a select statement.
     * By default the default projections is
     * {@link com.bingzer.android.dbv.IConfig#getIdNamingConvention()}
     * @param columns the columns to set
     */
    @Override
    public void setDefaultProjections(String... columns){
        if(columns == null || columns.length == 0)
            defaultProjections = new String[] { getIdNamingConvention() };
        else {
            for (String column : columns) {
                if (column.equalsIgnoreCase(getIdNamingConvention())) {
                    setIdNamingConvention(column);
                }
            }
            defaultProjections = columns;
        }
    }

    /**
     * Returns the default projections
     * @return projections
     */
    @Override
    public String[] getDefaultProjections(){
        return defaultProjections;
    }

    /**
     * Sets the default <code>Authority</code>
     * @param authority authority to set
     */
    @Override
    public void setDefaultAuthority(String authority){
        this.authority = authority;
    }

    /**
     * Returns the <code>authority</code>
     * @return Authority
     */
    @Override
    public String getDefaultAuthority(){
        return authority;
    }

    @Override
    public void setIdNamingConvention(String id) {
        String oldId = getIdNamingConvention();
        super.setIdNamingConvention(id);

        if(defaultProjections != null && defaultProjections.length > 0){
            for(int i = 0; i < defaultProjections.length; i++){
                if(defaultProjections[i].equalsIgnoreCase(oldId)){
                    defaultProjections[i] = id;
                }
            }
        }
        else setDefaultProjections();
    }
}
