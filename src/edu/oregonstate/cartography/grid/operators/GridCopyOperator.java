package edu.oregonstate.cartography.grid.operators;

import edu.oregonstate.cartography.grid.Grid;

/**
 * Copy a grid.
 * @author Bernie Jenny
 */
public class GridCopyOperator extends ThreadedGridOperator {

    @Override
    protected void operate(Grid src, Grid dst, int startRow, int endRow) {
        for (int row = startRow; row < endRow; ++row) {
            float[] srcArray = src.getGrid()[row];
            float[] dstArray = src.getGrid()[row];
            System.arraycopy(srcArray, 0, dstArray, 0, srcArray.length);
        }
    }

    @Override
    public String getName() {
        return "Copy";
    }
    
}
