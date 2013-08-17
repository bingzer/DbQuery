package com.bingzer.android.dbv.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingzer.android.dbv.DbQuery;
import com.bingzer.android.dbv.IDatabase;
import com.bingzer.android.dbv.IEntity;
import com.bingzer.android.dbv.IEntityList;
import com.bingzer.android.dbv.IQuery;
import com.bingzer.android.dbv.sqlite.SQLiteBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ricky on 8/14/13.
 */
public class EntityJoinTest extends AndroidTestCase {
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

        db.get("Orders").delete();
        db.get("Products").delete();
        db.get("Customers").delete();

        // initial value
        IQuery.InsertWith insert = db.get("Customers").insert("Name", "Address");
        insert.val("Baloteli", "Italy");
        insert.val("Pirlo", "Italy");
        insert.val("Ronaldo", "Portugal");
        insert.val("Messi", "Argentina");

        insert = db.get("Products").insert("Name", "Price");
        insert.val("Computer", 600);
        insert.val("Smartphone", 450);
        insert.val("Car", 20000);
        insert.val("House", 500000);
        insert.val("Monitor", 120);

        insert = db.get("Orders").insert("Quantity", "ProductId", "CustomerId");
        insert.val(2, db.get("Products").selectId("Name = ?", "Computer"), db.get("Customers").selectId("Name = ?", "Baloteli"));
        insert.val(1, db.get("Products").selectId("Name = ?", "House"), db.get("Customers").selectId("Name = ?", "Ronaldo"));
        insert.val(5, db.get("Products").selectId("Name = ?", "Monitor"), db.get("Customers").selectId("Name = ?", "Messi"));
        insert.val(1, db.get("Products").selectId("Name = ?", "Computer"), db.get("Customers").selectId("Name = ?", "Messi"));
    }

    public void testEntityJoin(){
        Order order = new Order();
        db.get("Orders O")
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


    static class OrderList extends LinkedList<Order> implements IEntityList<Order>{

        @Override
        public List<Order> getEntityList() {
            return this;
        }

        @Override
        public Order newEntity() {
            return new Order();
        }
    }

    static class Order implements IEntity {
        int id = -1;
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

        public void setId(int id){
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }

        /**
         * Determines how to map column and the class variable.
         *
         * @param mapper the mapper object
         */
        @Override
        public void map(Mapper mapper) {
            mapper.mapId(new Action<Integer>(Integer.class) {
                @Override
                public void set(Integer value) {
                    setId(value);
                }

                @Override
                public Integer get() {
                    return getId();
                }
            });

            mapper.map("Quantity", new Action<Integer>(Integer.class){

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

            mapper.map("ProductName", new Action<String>(String.class){

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

            mapper.map("CustomerName", new Action<String>(String.class){

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
