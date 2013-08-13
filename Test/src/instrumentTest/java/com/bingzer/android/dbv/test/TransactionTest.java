package com.bingzer.android.dbv.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

/**
 * Created by Ricky Tobing on 8/12/13.
 */
public class TransactionTest extends AndroidTestCase {
    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("TransactionDb");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return TransactionTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Person")
                        .addPrimaryKey("Id")
                        .add("Name", "String")
                        .add("Age", "Integer")
                        .add("Address", "Blob");
            }
        });

        db.get("Person").deleteAll();
    }

    public void testRollback(){
        IDatabase.Transaction transaction =db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {
                database.get("Person").insert("Name", "Age", "Address").val("NewPersonRollback", 100, "Batlimore".getBytes());

                Person p = new Person();
                database.get("Person").select("Name = ?", "NewPersonRollback").query(p);
                assertTrue(p.getId() > 0);
                assertTrue(p.getName().equals("NewPersonRollback"));
                assertTrue(p.getAge() == 100);
                throw new Error("Fake error so it will throw");
            }
        });
        try{
            transaction.commit();
        }
        catch (Throwable e){
            transaction.rollback();
        }
        finally {
            transaction.end();
        }

        Person p = new Person();
        db.get("Person").select("Name = ?", "NewPersonRollback").query(p);
        assertFalse(p.getId() > 0);
        assertTrue(p.getName() == null);
        assertFalse(p.getAge() == 100);
    }

    public void testCommit(){
        IDatabase.Transaction transaction = db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {
                database.get("Person").insert("Name", "Age", "Address").val("NewPersonCommit", 100, "Batlimore".getBytes());

                Person p = new Person();
                database.get("Person").select("Name = ?", "NewPersonCommit").query(p);
                assertTrue(p.getId() > 0);
                assertTrue(p.getName().equals("NewPersonCommit"));
                assertTrue(p.getAge() == 100);

            }
        });
        try{
            transaction.commit();
        }
        catch (Throwable e){
            transaction.rollback();
        }
        finally {
            transaction.end();
        }

        Person p = new Person();
        db.get("Person").select("Name = ?", "NewPersonCommit").query(p);
        assertTrue(p.getId() > 0);
        assertTrue(p.getName().equals("NewPersonCommit"));
        assertTrue(p.getAge() == 100);
    }

    public void testExecute_Success(){
        IDatabase.Transaction transaction = db.begin(new IDatabase.Batch() {
            @Override
            public void exec(IDatabase database) {
                database.get("Person").insert("Name", "Age", "Address").val("NewPersonExecute", 100, "Batlimore".getBytes());

                Person p = new Person();
                database.get("Person").select("Name = ?", "NewPersonExecute").query(p);
                assertTrue(p.getId() > 0);
                assertTrue(p.getName().equals("NewPersonExecute"));
                assertTrue(p.getAge() == 100);

            }
        });
        assertTrue(transaction.execute());

        Person p = new Person();
        db.get("Person").select("Name = ?", "NewPersonExecute").query(p);
        assertTrue(p.getId() > 0);
        assertTrue(p.getName().equals("NewPersonExecute"));
        assertTrue(p.getAge() == 100);
    }
}
