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

    private Database database;
    private IDatabase.Batch batch;

    TransactionImpl(Database database, IDatabase.Batch batch){
        this.database = database;
        this.batch = batch;
    }

    /**
     * Commit the transactions
     */
    @Override
    public void commit() {
        database.begin();
        batch.exec(database);
        database.commit();
    }

    /**
     * Rollback any transactions
     */
    @Override
    public void rollback() {
        database.rollback();
    }

    /**
     * Ends the transaction.
     * From this point on, all transaction has been set to
     * <code>autoCommit = true</code>
     */
    @Override
    public void end() {
        database.end();
    }

    /**
     * This method will do the following
     * <code>
     * <pre>
     *         try{
     *             transaction.commit();
     *             return true;
     *         }
     *         catch (Throwable any){
     *             transaction.rollback();
     *             return false;
     *         }
     *         finally{
     *             transaction.end();
     *         }
     *     </pre>
     * </code>
     *
     * @return true if the batch is successfully committed, false if otherwise
     */
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
