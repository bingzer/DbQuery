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
package com.bingzer.android.dbv.internal;

import com.bingzer.android.dbv.IView;
import com.bingzer.android.dbv.internal.queries.DropImpl;
import com.bingzer.android.dbv.queries.IQuery;

/**
 * Created by Ricky Tobing on 8/19/13.
 */
public class View extends Table implements IView {

    public View (Database db, String name){
        super(db, name);
    }

    @Override
    public IQuery<Boolean> drop() {
        db.enforceReadOnly();

        DropImpl query = new DropImpl();
        try{
            db.execSql("DROP VIEW " + getName());
            query.setValue(true);
        }
        catch (Exception e){
            query.setValue(false);
        }

        if(query.query()) db.removeTable(this);
        return query;
    }

}
