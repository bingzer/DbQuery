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

import com.bingzer.android.dbv.queries.Average;
import com.bingzer.android.dbv.queries.IFunction;
import com.bingzer.android.dbv.queries.Max;
import com.bingzer.android.dbv.queries.Min;
import com.bingzer.android.dbv.queries.Sum;
import com.bingzer.android.dbv.queries.Total;

/**
 * Created by Ricky Tobing on 7/20/13.
 */
class FunctionImpl implements IFunction {

    Object value;
    final StringBuilder builder;

    FunctionImpl(String functionName, String tableName, String columnName, String condition){
        builder = new StringBuilder("SELECT ")
                .append(functionName).append("(").append(columnName).append(") AS FN ")
                .append(" FROM ").append(tableName);

        if(condition != null){
            // append where if necessary
            if(!condition.toLowerCase().startsWith("where"))
                builder.append(" WHERE ");
            // safely prepare the where part
            builder.append(condition);
        }
    }

    ////////////////////////////////////////////
    ////////////////////////////////////////////

    @Override
    public int asInt() {
        try{
            return Integer.parseInt(asString());
        }
        catch (NumberFormatException e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public long asLong() {
        try{
            return Integer.parseInt(asString());
        }
        catch (NumberFormatException e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public float asFloat() {
        try{
            return Integer.parseInt(asString());
        }
        catch (NumberFormatException e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public double asDouble() {
        try{
            return Integer.parseInt(asString());
        }
        catch (NumberFormatException e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String asString() {
        return value == null ? "null" : value.toString();
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public String toString(){
        return builder.toString();
    }

    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    static class AverageImpl extends FunctionImpl implements Average {
        AverageImpl(String tableName, String columnName, String condition){
            super("AVG", tableName, columnName, condition);
        }
    }
    static class SumImpl extends FunctionImpl implements Sum {
        SumImpl(String tableName, String columnName, String condition){
            super("SUM", tableName, columnName, condition);
        }
    }
    static class TotalImpl extends FunctionImpl implements Total {
        TotalImpl(String tableName, String columnName, String condition){
            super("TOTAL", tableName, columnName, condition);
        }
    }
    static class MaxImpl extends FunctionImpl implements Max {
        MaxImpl(String tableName, String columnName, String condition){
            super("MAX", tableName, columnName, condition);
        }
    }
    static class MinImpl extends FunctionImpl implements Min {
        MinImpl(String tableName, String columnName, String condition){
            super("MIN", tableName, columnName, condition);
        }
    }

}
