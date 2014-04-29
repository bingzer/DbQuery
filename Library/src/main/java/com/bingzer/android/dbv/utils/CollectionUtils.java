/**
 * Copyright 2014 Ricky Tobing
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
package com.bingzer.android.dbv.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a collection utility tools
 */
public class CollectionUtils {

    /**
     * Count the 'size' of base iterable
     * @param iterable the iterable
     * @return the size
     */
    public static int size(Iterable<?> iterable){
        if(iterable instanceof Collection<?>)
            return ((Collection<?>) iterable).size();
        return size(iterable.iterator());
    }

    /**
     * Count the 'size' of base iterator
     * @param iterator the iterator
     * @return the size
     */
    public static int size(Iterator<?> iterator){
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }

    /**
     * Check to see if iterable contains a <code>value</code>.
     * The <code>equals()</code> will be performed to check the equality
     * @param iterable the target iterable
     * @param value the value
     * @param <T> generic type of value
     * @return true if it has <code>value</code>, false otherwise
     */
    public static <T> boolean contains(Iterable<T> iterable, T value){
        if(iterable instanceof Collection)
            return ((Collection) iterable).contains(value);
        return contains(iterable.iterator(), value);
    }

    /**
     * Check to see if iterable contains a <code>value</code>.
     * The <code>equals()</code> will be performed to check the equality
     * @param iterator the target iterator
     * @param value the value
     * @param <T> generic type of value
     * @return true if it has <code>value</code>, false otherwise
     */
    public static <T> boolean contains(Iterator<T> iterator, T value){
        while (iterator.hasNext()) {
            T next = iterator.next();
            if(next.equals(value)) return true;
        }

        return false;
    }

}
