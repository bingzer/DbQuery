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

import com.bingzer.android.dbv.IDatabase;

/**
 * Implements of {@link IDatabase.Transaction}
 *
 * Created by Ricky Tobing on 8/12/13.
 */
class TransactionImpl implements IDatabase.Transaction {

    final Database database;
    final IDatabase.Batch batch;

    TransactionImpl(Database database, IDatabase.Batch batch){
        this.database = database;
        this.batch = batch;
    }

    @Override
    public void commit() {
        synchronized (this){
            database.begin();
            batch.exec(database);
            database.commit();
        }
    }

    @Override
    public void rollback() {
        database.rollback();
    }

    @Override
    public void end() {
        database.end();
    }

    @Override
    public boolean execute() {
        try{
            commit();
            return true;
        }
        catch (Throwable e){
            rollback();
            return false;
        }
        finally {
            end();
        }
    }
}
