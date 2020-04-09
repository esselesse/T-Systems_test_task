package com.tsystems.javaschool.tasks.pyramid;

import java.util.Iterator;
import java.util.List;

public class PyramidBuilder {

    /**
     * Builds a pyramid with sorted values (with minumum value at the top line and maximum at the bottom,
     * from left to right). All vacant positions in the array are zeros.
     *
     * @param inputNumbers to be used in the pyramid
     * @return 2d array with pyramid inside
     * @throws {@link CannotBuildPyramidException} if the pyramid cannot be build with given input
     */
    public int[][] buildPyramid(List<Integer> inputNumbers) throws CannotBuildPyramidException {

        if(inputNumbers.contains(null)) {
            throw new CannotBuildPyramidException();
        }

        int counterOfNodes = 0;
        int lenOfInputNumbers = inputNumbers.size();
        int depthOfTheTree = 1;
        while (counterOfNodes < lenOfInputNumbers){
            counterOfNodes += depthOfTheTree;
            // every next row has a number of numbers more than in current row by one
            // so, the count of numbers in every row equal the index of that row
            if (counterOfNodes > lenOfInputNumbers || counterOfNodes < 0) {
                // well, if we have insufficient numbers to build a tree - or TOO MUCH, it is an exception
                throw new CannotBuildPyramidException();
            } else if(counterOfNodes == lenOfInputNumbers){
                break;
            }
            depthOfTheTree++;
        }
        if(depthOfTheTree > Integer.MAX_VALUE/2) {
            // just in case of emergency
            throw new CannotBuildPyramidException();
        }
        int widthOfTheTree = 2 * depthOfTheTree - 1;
        // because count of numbers in the last row is equal to 'depthOfTheTree';
        // and count of zeros is less than count of numbers by 1.
        // by the way, first number (on the top) is row['depthOfTheTree']
        // so, we have matrix 'depthOfTheTree' X 'widthOfTheTree'
        // first row contains only one number
        // every next row has by one number more than previous

        inputNumbers.sort((x, y) -> y - x);
        // it is sorted in DEscending order because i want to fill final array from down to up from right to left
        // unfortunately, i do not know how to simultaneously use lambdas for sorting and for catching duplicates
        // catching them by two iterators
        Iterator<Integer> iterator = inputNumbers.iterator();
        Iterator<Integer> iterator1 = inputNumbers.iterator();
        iterator.next();
        while (iterator.hasNext()){
            if(iterator.next().equals(iterator1.next())){
                throw new CannotBuildPyramidException();
            }
        }

        // finally, building tree array
        // remember that by default it autofills by zeros
        int[][] treeArray = new int[depthOfTheTree][widthOfTheTree];

        int numbersCountInARow = depthOfTheTree;
        iterator = inputNumbers.iterator();
        for (int i = depthOfTheTree - 1; i >= 0 ; i--) {
            for (int j = 0; j < numbersCountInARow; j++) {
                treeArray[i][widthOfTheTree - 1 - depthOfTheTree + numbersCountInARow - 2 * j] = iterator.next();
            }
            numbersCountInARow--;
        }

        return treeArray;
    }
}
