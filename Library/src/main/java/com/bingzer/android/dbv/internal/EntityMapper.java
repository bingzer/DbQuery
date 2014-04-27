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

import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.ITable;

import java.util.HashMap;

/**
 * Created by Ricky Tobing on 8/9/13.
 */
public class EntityMapper extends HashMap<String, IEntity.Action> implements IEntity.Mapper{
    private final ITable table;

    public EntityMapper(ITable table){
        this.table = table;
    }

    @Override
    public void mapId(IEntity.Action<Integer> action) {
        map(table.getColumnIdName(), action);
    }

    @Override
    public IEntity.Action get(String column) {
        return super.get(column);
    }

    @Override
    public void map(String column, IEntity.Action action) {
        put(column, action);
    }

}
