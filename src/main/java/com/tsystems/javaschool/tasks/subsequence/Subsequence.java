package com.tsystems.javaschool.tasks.subsequence;

import java.util.Iterator;
import java.util.List;

public class Subsequence {

    /**
     * Checks if it is possible to get a sequence which is equal to the first
     * one by removing some elements from the second one.
     *
     * @param x first sequence
     * @param y second sequence
     * @return <code>true</code> if possible, otherwise <code>false</code>
     */
    @SuppressWarnings("rawtypes")
    public boolean find(List x, List y) throws IllegalArgumentException{
        // assume that we are looking for elements of the FIRST list in the SECOND list,
        // not vice versa. So, y.size() >= x.size()

        if(x == null || y == null){
            throw new IllegalArgumentException();
        }

        Iterator xIterator = x.iterator(); // let's walk around the list using Iterators
        Iterator yIterator = y.iterator();
        Object tempX, tempY;    // we will store elements of iterators in this vars

        // if 'x' is empty, we can say that
        // the empty list 'x' is sublist of empty or non-empty list 'y'
        // and return 'true'
        // else take first element
        if(!x.iterator().hasNext()){
            return true;
        }
        else tempX = xIterator.next();

        // so, if 'x' is non-empty, but 'y' is empty, that is sad
        if(!y.iterator().hasNext()){
            return false;
        }

        while (yIterator.hasNext()){
            tempY = yIterator.next();

            if(tempX.equals(tempY)) {
                if(xIterator.hasNext()){
                    tempX = xIterator.next();
                }
                else return true;
            }
        }
        return false;
    }
}
