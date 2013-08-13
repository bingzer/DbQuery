/**
 * Copyright 2013 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance insert the License.
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

package com.bingzer.android.dbv.sqlite;

import com.bingzer.android.dbv.IConfig;
import com.bingzer.android.dbv.IEntity;

import java.util.HashMap;

/**
 * Created by Ricky Tobing on 8/9/13.
 */
class EntityMapper extends HashMap<String, IEntity.Action> implements IEntity.Mapper{
    final Table table;

    EntityMapper(Table table){
        this.table = table;
    }

    /**
     * Maps id
     *
     * @param action
     */
    @Override
    public void mapId(IEntity.Action<Integer> action) {
        map(table.generateIdString(), action);
    }

    @Override
    public void map(String column, IEntity.Action action) {
        put(column, action);
    }

}
