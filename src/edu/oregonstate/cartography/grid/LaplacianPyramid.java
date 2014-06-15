package edu.oregonstate.cartography.grid;

import edu.oregonstate.cartography.grid.operators.GridScaleOperator;
import java.util.Arrays;

/**
 *
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class LaplacianPyramid {
    
    private Grid[] levels;
    
    /**
     * weights to merge pyramid levels
     */
    private float[] w;
    
    private static final float wa = 0.4f;
    private static final float wb = 0.25f;
    private static final float wc = 0.05f;

    public void createPyramid(Grid[] gaussianPyramid) {

        levels = new Grid[gaussianPyramid.length];

        // store the smallest Gaussian grid in the Laplacian pyramid
        levels[levels.length - 1] = gaussianPyramid[gaussianPyramid.length - 1];

        // compute the levels of this Laplacian pyramid by computing differences
        // between the levels of the Gaussian pyramid.
        for (int i = gaussianPyramid.length - 1; i > 0; i--) {

            Grid nextLargerGrid = gaussianPyramid[i - 1];

            // expand the smaller grid to the size of the larger grid
            Grid expanded = LaplacianPyramid.expand(gaussianPyramid[i],
                    nextLargerGrid.getCols(), nextLargerGrid.getRows());

            // compute the difference
            levels[i - 1] = LaplacianPyramid.difGrids(nextLargerGrid, expanded);
        }

    }

    private static void expandBorderColumns(Grid src, float[][] dst) {

        final int cols = src.getCols();
        final int rows = src.getRows();

        // left column
        for (int r = 0; r < rows; r++) {
            float v0 = src.getValue(0, r);
            float v1 = v0;
            float v2 = src.getValue(1, r);
            float vEven = 2.f * (wc * (v0 + v2) + wa * v1);
            float vOdd = 2.f * wb * (v1 + v2);

            if (Float.isNaN(vEven) || Float.isNaN(vOdd)) {
                if (Float.isNaN(vEven) && Float.isNaN(vOdd)) {
                    dst[r][0] = Float.NaN;
                    dst[r][1] = Float.NaN;
                } else {
                    expandWithVoid(src.getGrid(), dst, 0, r, true);
                }
            } else {
                dst[r][0] = vEven;
                dst[r][1] = vOdd;
            }

        }

        // right column
        for (int r = 0; r < rows; r++) {
            float v0 = src.getValue(cols - 2, r);
            float v1 = src.getValue(cols - 1, r);
            float v2 = v1;
            float vEven = 2.f * (wc * (v0 + v2) + wa * v1);
            float vOdd = 2.f * wb * (v1 + v2);
            int c = (cols - 1) * 2;

            if (Float.isNaN(vEven) || Float.isNaN(vOdd)) {
                if (Float.isNaN(vEven) && Float.isNaN(vOdd)) {
                    dst[r][c] = Float.NaN;
                    dst[r][c + 1] = Float.NaN;
                } else {
                    expandWithVoid(src.getGrid(), dst, c, r, true);
                }
            } else {
                dst[r][c] = vEven;
                dst[r][c + 1] = vOdd;
            }
        }

    }

    private static void expandBorderRows(float[][] src, Grid dstGeoGrid) {

        final int cols = dstGeoGrid.getCols();
        final int rows = dstGeoGrid.getRows();
        final float[][] dstGrid = dstGeoGrid.getGrid();

        // top row
        for (int c = 0; c < cols; c++) {
            float v0 = src[0][c];
            float v1 = v0;
            float v2 = src[1][c];
            float vEven = 2.f * (wc * (v0 + v2) + wa * v1);
            float vOdd = 2.f * wb * (v1 + v2);
            if (Float.isNaN(vEven) || Float.isNaN(vOdd)) {
                if (Float.isNaN(vEven) && Float.isNaN(vOdd)) {
                    dstGrid[0][c] = Float.NaN;
                    dstGrid[1][c] = Float.NaN;
                } else {
                    expandWithVoid(dstGeoGrid.getGrid(), dstGrid, c, 0, false);
                }
            } else {
                dstGrid[0][c] = vEven;
                dstGrid[1][c] = vOdd;
            }

        }

        // bottom row
        for (int c = 0; c < cols; c++) {
            //float v0 = src[rows / 2 - 2][c];
            //float v1 = src[rows / 2 - 1][c];
            float v0 = src[src.length - 2][c];
            float v1 = src[src.length - 1][c];
            float v2 = v1;
            float vEven = 2.f * (wc * (v0 + v2) + wa * v1);
            float vOdd = 2.f * wb * (v1 + v2);
            if (Float.isNaN(vEven) || Float.isNaN(vOdd)) {
                if (Float.isNaN(vEven) && Float.isNaN(vOdd)) {
                    dstGrid[rows - 2][c] = Float.NaN;
                    dstGrid[rows - 1][c] = Float.NaN;
                } else {
                    expandWithVoid(dstGeoGrid.getGrid(), dstGrid, c, rows - 2, false);
                }
            } else {
                dstGrid[rows - 2][c] = vEven;
                dstGrid[rows - 1][c] = vOdd;
            }
        }
    }

    /**
     * Expand the size of a grid by a factor 2.
     *
     * @param grid The grid to expand.
     * @param maxCols
     * @param maxRows
     * @return
     */
    public static Grid expand(Grid grid, int maxCols, int maxRows) {

        final int cols = grid.getCols();
        final int rows = grid.getRows();

        // the new grid is twice as large
        final int newCols = Math.min(maxCols, cols * 2);
        final int newRows = Math.min(maxRows, rows * 2);

        Grid expandedGrid = new Grid(newCols, newRows, grid.getCellSize() / 2);
        //expandedGrid.setWest(geoGrid.getWest());
        //expandedGrid.setNorth(geoGrid.getNorth());

        // tempGrid holds an intermediate grid that is expanded horizontally, 
        // but not vertically.
        float[][] tempGrid = new float[rows][cols * 2];

        LaplacianPyramid.expandBorderColumns(grid, tempGrid);
        for (int r = 0; r < rows; r++) {
            final float[] tempGridRow = tempGrid[r];
            for (int c = 1; c < cols - 1; c++) {
                final float v0 = grid.getValue(c - 1, r);
                final float v1 = grid.getValue(c, r);
                final float v2 = grid.getValue(c + 1, r);
                final float vEven = 2.f * (wc * (v0 + v2) + wa * v1);
                final float vOdd = 2.f * wb * (v1 + v2);
                if (Float.isNaN(vEven) || Float.isNaN(vOdd)) {
                    if (Float.isNaN(vEven) && Float.isNaN(vOdd)) {
                        tempGridRow[c * 2] = Float.NaN;
                        tempGridRow[c * 2 + 1] = Float.NaN;
                    } else {
                        expandWithVoid(grid.getGrid(), tempGrid, c, r, true);
                    }
                } else {
                    tempGridRow[c * 2] = vEven;
                    tempGridRow[c * 2 + 1] = vOdd;
                }
            }
        }

        LaplacianPyramid.expandBorderRows(tempGrid, expandedGrid);
        for (int r = 1; r < rows - 1; r++) {
            for (int c = 0; c < newCols; c++) {

                final float v0 = tempGrid[r - 1][c]; //tempGrid[(r - 1) * cols * 2 + c];
                final float v1 = tempGrid[r][c];
                final float v2 = tempGrid[r + 1][c];
                final float vEven = 2.f * (wc * (v0 + v2) + wa * v1);
                final float vOdd = 2.f * wb * (v1 + v2);
                if (Float.isNaN(vEven) || Float.isNaN(vOdd)) {
                    if (Float.isNaN(vEven) && Float.isNaN(vOdd)) {
                        expandedGrid.setValue(Float.NaN, c, 2 * r);
                        expandedGrid.setValue(Float.NaN, c, 2 * r + 1);
                    } else {
                        expandWithVoid(tempGrid, expandedGrid.getGrid(), c / 2, r, false);
                    }
                } else {
                    expandedGrid.setValue(vEven, c, 2 * r);
                    expandedGrid.setValue(vOdd, c, 2 * r + 1);
                }
            }
        }

        return expandedGrid;
    }

    private static void expandWithVoid(float[][] srcGrid,
            float[][] expandedGrid,
            int c,
            int r,
            boolean horizontal) {

        final float v0, v1, v2;
        if (horizontal) {
            v0 = srcGrid[r][c - 1];
            v1 = srcGrid[r][c];
            v2 = srcGrid[r][c + 1];
        } else {
            v0 = srcGrid[r - 1][c];
            v1 = srcGrid[r][c];
            v2 = srcGrid[r + 1][c];
        }

        float vEven = 0f;
        float vOdd = 0f;
        float totEvenW = 0f;
        float totOddW = 0f;

        if (!Float.isNaN(v0)) {
            vEven = wc * v0;
            totEvenW = wc;
        }
        if (!Float.isNaN(v1)) {
            vEven += wa * v1;
            vOdd += wb * v1;
            totEvenW += wa;
            totOddW += wb;
        }
        if (!Float.isNaN(v2)) {
            vEven += wc * v2;
            vOdd += wb * v2;
            totEvenW += wc;
            totOddW += wb;
        }

        if (totEvenW == 0) {
            vEven = Float.NaN;
        }
        if (totOddW == 0) {
            vOdd = Float.NaN;
        }
        final float scaleEven = (wc * 2 + wa) / totEvenW;
        final float scaleOdd = wb * 2 / totOddW;

        vEven *= 2f * scaleEven;
        vOdd *= 2f * scaleOdd;
        if (horizontal) {
            expandedGrid[r][c * 2] = vEven;
            expandedGrid[r][c * 2 + 1] = vOdd;
        } else {
            expandedGrid[r * 2][c] = vEven;
            expandedGrid[r * 2 + 1][c] = vOdd;
        }
    }

    /**
     * Adds a high frequency grid to a low frequency grid.
     *
     * @param lowFreqSum Low frequency grid. The high frequency grid will be
     * added to this grid.
     * @param highFreq High frequency grid.
     * @param scale
     */
    public void sumGrids(Grid lowFreqSum, Grid highFreq, float scale) {

        if (!lowFreqSum.isIdenticalInSize(highFreq)) {
            throw new IllegalArgumentException("grids of different size cannot be summed");
        }

        final int cols = lowFreqSum.getCols();
        final int rows = lowFreqSum.getRows();

        for (int r = 0; r < rows; r++) {
            final float[] g1row = lowFreqSum.getGrid()[r];
            final float[] g2row = highFreq.getGrid()[r];
            for (int c = 0; c < cols; c++) {
                lowFreqSum.setValue(g1row[c] + g2row[c] * scale, c, r);
            }
        }
    }

    /**
     * Compute the difference between two grids. Grids must be of identical
     * size.
     *
     * @param grid1
     * @param grid2
     * @return
     */
    public static Grid difGrids(Grid grid1, Grid grid2) {

        if (!grid1.isIdenticalInSize(grid2)) {
            throw new IllegalArgumentException("grids of different size");
        }

        final int cols = grid1.getCols();
        final int rows = grid1.getRows();
        Grid difGrid = new Grid(cols, rows, grid1.getCellSize());
        // FIXME
        //difGrid.setWest(grid1.getWest());
        //difGrid.setNorth(grid1.getNorth());

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                final float v1 = grid1.getValue(c, r);
                final float v2 = grid2.getValue(c, r);
                difGrid.setValue(v1 - v2, c, r);
            }
        }

        return difGrid;
    }

    public void setWeights(float[] w) {
        this.w = Arrays.copyOf(w, w.length);
    }
    
    /**
     * Sums the levels of the pyramid to re-synthesize the original image.
     *
     * @return
     */
    public Grid sumLevels() {
        
        // copy the smallest grid of the pyramid
        Grid sum = this.levels[this.levels.length - 1].clone();

        // The base level weight is always 1.0 (base raised to the power of 0)
        new GridScaleOperator(1.0F).operate(sum);

        // expand the sum and and add the next larger grids
        for (int i = this.levels.length - 2; i >= 0; i--) {
            Grid grid = this.levels[i];
            sum = LaplacianPyramid.expand(sum, grid.getCols(), grid.getRows());
            // System.out.println("level: " + i + ",\t weight: " + (w == null ? Float.NaN : w[i]));
            sumGrids(sum, grid, w == null ? 1f : w[i]);
        }
        //return altGrid;
        return sum;

    }

    public Grid[] getLevels() {
        return levels;
    }
}
