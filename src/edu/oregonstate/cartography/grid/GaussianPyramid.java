package edu.oregonstate.cartography.grid;

import java.util.ArrayList;

/**
 * Gaussian pyramid.
 *
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class GaussianPyramid {

    private static int MIN_SIDE_LENGTH = 2;
    private final Grid[] pyramid;


    public static Grid[] createPyramid(Grid geoGrid, int maxLevelsCount) {
        return GaussianPyramid.createPyramid(geoGrid, maxLevelsCount,
                MIN_SIDE_LENGTH * MIN_SIDE_LENGTH);
    }

    public static Grid[] createPyramid(Grid geoGrid,
            int maxLevelsCount,
            int minCellCount) {

        ArrayList<Grid> pyramid = new ArrayList<>();
        pyramid.add(geoGrid);
        Convolution5x5 conv = new Convolution5x5();
        for (;;) {
            if (geoGrid == null) {
                break;
            }
            int newCols = geoGrid.getCols() / 2;
            int newRows = geoGrid.getRows() / 2;
            if (newCols <= MIN_SIDE_LENGTH
                    || newRows <= MIN_SIDE_LENGTH
                    || newCols * newRows < minCellCount
                    || pyramid.size() == maxLevelsCount) {
                break;
            }
            geoGrid = conv.convolveToHalfSize(geoGrid);
            pyramid.add(geoGrid);
        }
        return pyramid.toArray(new Grid[pyramid.size()]);
    }

    public GaussianPyramid(Grid geoGrid) {
        this.pyramid = GaussianPyramid.createPyramid(geoGrid, 9999);
    }

    public GaussianPyramid(Grid geoGrid, int maxLevelsCount) {
        this.pyramid = GaussianPyramid.createPyramid(geoGrid, maxLevelsCount);
    }

    public GaussianPyramid(Grid geoGrid, int maxLevelsCount, int minCellCount) {
        this.pyramid = GaussianPyramid.createPyramid(geoGrid, maxLevelsCount, minCellCount);
    }

    public Grid[] getPyramid() {
        return this.pyramid;
    }
    
    public Grid[] getExpandedPyramid() {
        Grid[] expandedPyramid = new Grid[pyramid.length];
        for (int i = 0; i < pyramid.length; i++) {
            expandedPyramid[i] = pyramid[i];
            for (int k = 0; k < i; k++) {
                expandedPyramid[i] = LaplacianPyramid.expand(expandedPyramid[i], 
                        expandedPyramid[i].getCols() * 2,
                        expandedPyramid[i].getRows() * 2);
            }
        }
        return expandedPyramid;
    }

    public Grid getFullResolutionLevel() {
        return this.pyramid[0];
    }

    /**
     * Returns a grid at a specified pyramid level. The full resolution grid has
     * level 0, the lowest resolution grid has level getLevelsCount() - 1
     *
     * @param level
     * @return Grid at requested level
     */
    public Grid getLevel(int level) {
        return this.pyramid[level];
    }

    public int getLevelsCount() {
        return this.pyramid.length;
    }

    public float getValue(int col, int row, int pyramidLevel) {
        return this.pyramid[pyramidLevel].getValue(col, row);
    }
}
