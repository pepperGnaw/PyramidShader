package edu.oregonstate.cartography.grid.operators;

import edu.oregonstate.cartography.grid.Grid;

// FIXME should be multi-threaded

public class GridDiffDivOperator implements GridOperator{
    
    public GridDiffDivOperator() {
    }
  
    @Override
    public String getName() {
        return "Difference & Division";
    }

    public Grid operate(Grid grid1, Grid grid2, Grid grid3) {
        if (grid1 == null || grid2 == null || grid3 == null)
            throw new IllegalArgumentException();
        
        final int nrows = grid1.getRows();
        final int ncols = grid1.getCols();
        Grid newGrid = new Grid(ncols, nrows, grid1.getCellSize());
        newGrid.setWest(grid1.getWest());
        newGrid.setSouth(grid1.getSouth());
        
        float[][] srcGrid1 = grid1.getGrid();
        float[][] srcGrid2 = grid2.getGrid();
        float[][] srcGrid3 = grid3.getGrid();
        float[][] dstGrid = newGrid.getGrid();
        
        for (int row = 0; row < nrows; ++row) {
            float[] srcRow1 = srcGrid1[row];
            float[] srcRow2 = srcGrid2[row];
            float[] srcRow3 = srcGrid3[row];
            float[] dstRow = dstGrid[row];
            for (int col = 0; col < ncols; ++col) {
                float v = (srcRow1[col] - srcRow2[col]) / (srcRow3[col] + 1f);
                dstRow[col] = Float.isInfinite(v) ? Float.NaN : v;
            }
        }
        return newGrid;
    }

    @Override
    public Grid operate(Grid grid) {
        throw new UnsupportedOperationException();
    }

}
