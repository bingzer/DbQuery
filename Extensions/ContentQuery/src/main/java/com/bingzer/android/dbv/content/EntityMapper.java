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
package com.bingzer.android.dbv.content;

import com.bingzer.android.dbv.Delegate;
import com.bingzer.android.dbv.IEntity;

import java.util.HashMap;

/**
 * Created by Ricky Tobing on 8/9/13.
 */
class EntityMapper extends HashMap<String, Delegate> implements IEntity.Mapper {
    final IBaseResolver resolver;

    EntityMapper(IBaseResolver resolver){
        this.resolver = resolver;
    }

    @Override
    public void mapId(Delegate<Integer> delegate) {
        map(resolver.generateIdString(), delegate);
    }

    @Override
    public Delegate get(String column) {
        return super.get(column);
    }

    @Override
    public void map(String column, Delegate delegate) {
        put(column, delegate);
    }

}
