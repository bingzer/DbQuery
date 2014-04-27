/**
 * Copyright 2014 Ricky Tobing
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
package com.bingzer.android.dbv.internal.queries;

import com.bingzer.android.dbv.queries.IFunction;

/**
 * Created by Ricky Tobing on 7/20/13.
 */
public class FunctionImpl implements IFunction {

    private double value;
    private final StringBuilder builder;

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

    ////////////////////////////////////////////////////////////////////////////////////////

    public void setValue(double value){
        this.value = value;
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int asInt() {
        try{
            return (int) value;
        }
        catch (NumberFormatException e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public long asLong() {
        try{
            return (long) value;
        }
        catch (NumberFormatException e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public float asFloat() {
        try{
            return (float) value;
        }
        catch (NumberFormatException e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public double asDouble() {
        try{
            return value;
        }
        catch (NumberFormatException e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String asString() {
        return value() == null ? null : value().toString();
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public String toString(){
        return builder.toString();
    }

}
