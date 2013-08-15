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

import com.bingzer.android.dbv.IFunction;

/**
 * Created by Ricky Tobing on 7/20/13.
 */
public class FunctionImpl implements IFunction {

    String functionName;
    Object value;
    StringBuilder builder;

    FunctionImpl(String functionName, String tableName, String columnName){
        this.functionName = functionName;
        this.builder = new StringBuilder();
        builder.append("SELECT ")
                .append(functionName).append("(").append(columnName).append(") AS FN ")
                .append(" FROM ").append(tableName);
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

    public static class AverageImpl extends FunctionImpl implements Average {
        AverageImpl(String tableName, String columnName){
            super("AVG", tableName, columnName);
        }
    }
    public static class SumImpl extends FunctionImpl implements Sum {
        SumImpl(String tableName, String columnName){
            super("SUM", tableName, columnName);
        }
    }
    public static class MaxImpl extends FunctionImpl implements Max {
        MaxImpl(String tableName, String columnName){
            super("MAX", tableName, columnName);
        }
    }
    public static class MinImpl extends FunctionImpl implements Min {
        MinImpl(String tableName, String columnName){
            super("MIN", tableName, columnName);
        }
    }

}
