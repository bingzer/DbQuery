package com.bingzer.android.dbv.test;

import android.content.Context;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.Delegate;
import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.queries.IEnumerable;
import com.bingzer.android.dbv.SQLiteBuilder;
import com.bingzer.android.dbv.queries.InsertInto;

import java.util.LinkedList;

/**
 * Created by Ricky on 8/14/13.
 */
public class EntityJoinTest extends AndroidTestCase {
    static boolean populated = false;
    IDatabase db;

    @Override
    public void setUp(){
        db = DbQuery.getDatabase("EntityJoinTestDb");
        db.open(1, new SQLiteBuilder() {
            @Override
            public Context getContext() {
                return EntityJoinTest.this.getContext();
            }

            @Override
            public void onModelCreate(IDatabase database, IDatabase.Modeling modeling) {
                modeling.add("Orders")
                        .addPrimaryKey("Id")
                        .add("Quantity", "INTEGER")
                        .add("ProductId", "INTEGER")
                        .add("CustomerId", "INTEGER");

                modeling.add("Products")
                        .addPrimaryKey("Id")
                        .add("Name", "TEXT")
                        .add("Price", "FLOAT");

                modeling.add("Customers")
                        .addPrimaryKey("Id")
                        .add("Name", "TEXT")
                        .add("Address", "TEXT");
            }
        });

        if(!populated){
            db.from("Orders").delete();
            db.from("Products").delete();
            db.from("Customers").delete();

            // initial value
            InsertInto insert = db.from("Customers").insertInto("Name", "Address");
            insert.val("Baloteli", "Italy");
            insert.val("Pirlo", "Italy");
            insert.val("Ronaldo", "Portugal");
            insert.val("Messi", "Argentina");

            insert = db.from("Products").insertInto("Name", "Price");
            insert.val("Computer", 600);
            insert.val("Smartphone", 450);
            insert.val("Car", 20000);
            insert.val("House", 500000);
            insert.val("Monitor", 120);

            insert = db.from("Orders").insertInto("Quantity", "ProductId", "CustomerId");
            insert.val(2, db.from("Products").selectId("Name = ?", "Computer"), db.from("Customers").selectId("Name = ?", "Baloteli"));
            insert.val(1, db.from("Products").selectId("Name = ?", "House"), db.from("Customers").selectId("Name = ?", "Ronaldo"));
            insert.val(5, db.from("Products").selectId("Name = ?", "Monitor"), db.from("Customers").selectId("Name = ?", "Messi"));
            insert.val(1, db.from("Products").selectId("Name = ?", "Computer"), db.from("Customers").selectId("Name = ?", "Messi"));

            populated = true;
        }
    }

    public void testEntityJoin(){
        Order order = new Order();
        db.from("Orders O")
                .join("Products P", "P.Id = O.ProductId")
                .join("Customers C", "C.Id = O.CustomerId")
                .select("C.Name = ? AND P.Name = ?", "Messi", "Monitor")
                .columns("O.Id AS Id", "Quantity", "P.Name AS ProductName", "C.Name AS CustomerName")
                .query(order);

        assertTrue(order.getCustomerName().equals("Messi"));
        assertTrue(order.getProductName().equals("Monitor"));
        assertTrue(order.getId() > 0);
        assertTrue(order.getQuantity() == 5);
    }

    public void testEntityJoin_Enumerable(){
        db.from("Orders O")
                .join("Products P", "P.Id = O.ProductId")
                .join("Customers C", "C.Id = O.CustomerId")
                .select("C.Name = ? AND P.Name = ?", "Messi", "Monitor")
                .columns("O.Id AS Id", "Quantity", "P.Name AS ProductName", "C.Name AS CustomerName")
                .query(new IEnumerable<Cursor>() {
                    @Override
                    public void next(Cursor cursor) {
                        assertEquals("Messi", cursor.getString(cursor.getColumnIndex("CustomerName")));
                        assertEquals("Monitor", cursor.getString(cursor.getColumnIndex("ProductName")));
                        assertTrue(cursor.getInt(cursor.getColumnIndex("Id")) > 0);
                        assertEquals(5, cursor.getInt(cursor.getColumnIndex("Quantity")));
                    }
                });
    }


    static class OrderList extends LinkedList<Order> implements IEntityList<Order>{

        @Override
        public Order newEntity() {
            return new Order();
        }
    }

    static class Order implements IEntity {
        long id = -1;
        int quantity;
        // from product customerTable
        String productName;
        // from customer customerTable
        String customerName;

        /////////////////////////////

        String getCustomerName() {
            return customerName;
        }

        void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        String getProductName() {
            return productName;
        }

        void setProductName(String productName) {
            this.productName = productName;
        }

        int getQuantity() {
            return quantity;
        }

        void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setId(long id){
            this.id = id;
        }

        @Override
        public long getId() {
            return id;
        }

        /**
         * Determines how to map column and the class variable.
         *
         * @param mapper the mapper object
         */
        @Override
        public void map(Mapper mapper) {
            mapper.mapId(new Delegate.TypeId(this) {
                @Override
                public void set(Long value) {
                    setId(value);
                }
            });

            mapper.map("Quantity", new Delegate.TypeInteger(){

                /**
                 * Sets the value
                 *
                 * @param value the value to set
                 */
                @Override
                public void set(Integer value) {
                    setQuantity(value);
                }

                /**
                 * Returns the value
                 *
                 * @return the value
                 */
                @Override
                public Integer get() {
                    return getQuantity();
                }
            });

            mapper.map("ProductName", new Delegate.TypeString(){

                /**
                 * Sets the value
                 *
                 * @param value the value to set
                 */
                @Override
                public void set(String value) {
                    setProductName(value);
                }

                /**
                 * Returns the value
                 *
                 * @return the value
                 */
                @Override
                public String get() {
                    return getProductName();
                }
            });

            mapper.map("CustomerName", new Delegate.TypeString(){

                /**
                 * Sets the value
                 *
                 * @param value the value to set
                 */
                @Override
                public void set(String value) {
                    setCustomerName(value);
                }

                /**
                 * Returns the value
                 *
                 * @return the value
                 */
                @Override
                public String get() {
                    return getCustomerName();
                }
            });
        }
    }
}
