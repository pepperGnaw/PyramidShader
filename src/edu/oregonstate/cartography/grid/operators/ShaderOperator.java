package edu.oregonstate.cartography.grid.operators;

import edu.oregonstate.cartography.app.Vector3D;
import edu.oregonstate.cartography.grid.Grid;

/**
 * This operator computes shaded relief from a terrain model.
 *
 * @author Charles Preppernau and Bernie Jenny, Oregon State University
 */
public class ShaderOperator extends ThreadedGridOperator {

    // vertical exaggeration factor applied to terrain values before computing a 
    // shading value
    private double vertExaggeration;

    /**
     * Azimuth of the light. Counted from north in counter-clock-wise direction.
     * Between 0 and 360 degrees.
     */
    private int illuminationAzimuth = 315;

    /**
     * The vertical angle of the light direction from the zenith towards the
     * horizon. Between 0 and 90 degrees.
     */
    private int illuminationZenith = 45;

    /**
     * Creates a new instance
     */
    public ShaderOperator() {
    }

    /**
     * Compute vector product of v1 and v2, and add resulting vector to destV.
     * This avoids the creation of a new vector object.
     *
     * @param v1x
     * @param v1y
     * @param v1z
     * @param v2x
     * @param v2y
     * @param v2z
     * @param destV destination vector
     */
    public static void addVectorProduct(double v1x, double v1y, double v1z,
            double v2x, double v2y, double v2z, Vector3D destV) {
        destV.x += v1y * v2z - v1z * v2y;
        destV.y += v1z * v2x - v1x * v2z;
        destV.z += v1x * v2y - v1y * v2x;
    }

    /**
     * Compute a normal vector for a point on a digital elevation model. The
     * length of the normal vector is 1.
     *
     * @param col The horizontal coordinate at which a normal must be computed.
     * @param row The vertical coordinate at which a normal must be computed.
     * @param grid Grid with elevation values
     * @param n The vector that will receive the resulting normal. Invalid upon
     * start, only used to avoid creation of a new Vector3D object.
     * @param cellSize The cell size in meters. Should not be in degrees.
     * <B>Important: row is counted from top to bottom.</B>
     */
    private void computeTerrainNormal(int col, int row, Grid grid, Vector3D n, double cellSize) {
        float[][] g = grid.getGrid();

        //Make sure the point is inside the grid and not on the border of the grid.       
        if (col > 0
                && col < grid.getCols() - 1
                && row > 0
                && row < grid.getRows() - 1) {

            // get height values
            double elevCenter = g[row][col];
            double elevS = (g[row + 1][col] - elevCenter) * vertExaggeration;
            double elevE = (g[row][col + 1] - elevCenter) * vertExaggeration;
            double elevN = (g[row - 1][col] - elevCenter) * vertExaggeration;
            double elevW = (g[row][col - 1] - elevCenter) * vertExaggeration;

            // sum vector products, one for each quadrant
            // sourth x east
            Vector3D.vectorProduct(0, -cellSize, elevS, cellSize, 0, elevE, n);
            // east x north
            addVectorProduct(cellSize, 0, elevE, 0, cellSize, elevN, n);
            // north x west
            addVectorProduct(0, cellSize, elevN, -cellSize, 0, elevW, n);
            // west x south
            addVectorProduct(-cellSize, 0, elevW, 0, -cellSize, elevS, n);

            // normalize and return vector
            n.normalize();
        } else {
            // border pixels are stuck with a level surface.
            n.x = 0;
            n.y = 0;
            n.z = 1;
        }
    }

    /**
     * Compute a shading for a chunk of the grid.
     *
     * @param src Source grid
     * @param dst Destination grid
     * @param startRow First row.
     * @param endRow First row of next chunk.
     */
    @Override
    protected void operate(Grid src, Grid dst, int startRow, int endRow) {
        int cols = src.getCols();

        // create a light vector
        Vector3D light = new Vector3D(illuminationAzimuth, illuminationZenith);

        // create a normal vector, and re-use it for every pixel
        Vector3D n = new Vector3D(0, 0, 0);

        // the cell size to calculate the horizontal components of vectors
        double cellSize = src.getCellSize();
        // convert degrees to meters on a sphere
        if (cellSize < 0.1) {
            cellSize = cellSize / 180 * Math.PI * 6371000;
        }
        
        // Loop through each grid cell
        float[][] dstGrid = dst.getGrid();
        for (int row = startRow; row < endRow; ++row) {
            float[] dstRow = dstGrid[row];
            for (int col = 0; col < cols; col++) {
                // compute the normal of the cell
                computeTerrainNormal(col, row, src, n, cellSize);

                // compute the dot product of the normal and the light vector. This
                // gives a value between -1 (surface faces directly away from
                // light) and 1 (surface faces directly toward light)
                double dotProduct = n.dotProduct(light);

                // scale dot product from [-1, +1] to a gray value in [0, 255]
                dstRow[col] = (float) ((dotProduct + 1) / 2 * 255.0D);
            }
        }
    }

    @Override
    public String getName() {
        return "Shading";
    }

    /**
     * @param illuminationAzimuth the illuminationAzimuth to set
     */
    public void setIlluminationAzimuth(int illuminationAzimuth) {
        this.illuminationAzimuth = illuminationAzimuth;
    }

    /**
     * @param illuminationZenith the illuminationZenith to set
     */
    public void setIlluminationZenith(int illuminationZenith) {
        this.illuminationZenith = illuminationZenith;
    }

    public void setVerticalExaggeration(double ve) {
        this.vertExaggeration = ve;
    }
}
