package edu.oregonstate.cartography.grid.operators;

import edu.oregonstate.cartography.grid.Grid;
import edu.oregonstate.cartography.gui.SwingWorkerWithProgressIndicator;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.SwingWorker;

/**
 * Computes illuminated contour lines from a digital elevation model.
 *
 * @author Jim Eynard and Bernhard Jenny, Cartography and Geovisualization
 * Group, Oregon State University
 */
public class IlluminatedContoursOperator extends ThreadedGridOperator {

    public final int CONTOURS_TRANSPARENT = -1;

    // a SwingWorker for communicating progress and for checking cancel events
    private SwingWorker progress;

    // this image will receive the computed contour lines
    private BufferedImage image;

    // illuminated and shaded or only shaded contours
    private final boolean illuminated;

    // width of shaded lines
    private final double shadowWidth;

    // width of illuminated lines
    private final double illuminatedWidth;

    // minimum line width
    private final double minWidth;

    // Tanaka transition betweeen shaded and illuminated contours
    private final boolean tanaka;

    // azimuth of illumination from north in clockwise direction in degrees
    private final double azimuth;

    // contour interval
    private final double interval;

    private final int gradientAngle;

    // gray value of illuminated contours
    private final int illluminatedGray;
    private int[] imageBuffer;

    /**
     *
     * @param illuminated
     * @param shadowWidth
     * @param illuminatedWidth
     * @param minWidth
     * @param tanaka
     * @param azimuth
     * @param interval
     * @param gradientAngle
     * @param illluminatedGray
     */
    public IlluminatedContoursOperator(boolean illuminated,
            double shadowWidth,
            double illuminatedWidth,
            double minWidth,
            boolean tanaka,
            double azimuth,
            double interval,
            int gradientAngle,
            int illluminatedGray) {
        this.illuminated = illuminated;
        this.shadowWidth = shadowWidth;
        this.illuminatedWidth = illuminatedWidth;
        this.minWidth = minWidth;
        this.tanaka = tanaka;
        this.azimuth = azimuth;
        this.interval = interval;
        this.gradientAngle = gradientAngle;
        this.illluminatedGray = illluminatedGray;

    }

    /**
     * Renders contours to the passed image.
     *
     * @param destinationImage Image must be this.scale times larger than the
     * grid.
     * @param grid Grid with elevation values.
     * @param slopeGrid Grid with slope values.
     * @param progress Progress indicator. Not used when scale is 1.
     */
    public void renderToImage(BufferedImage destinationImage, Grid grid, Grid slopeGrid, SwingWorker progress) {
        if (destinationImage == null) {
            throw new IllegalArgumentException();
        }
        this.image = destinationImage;
        this.progress = progress;
        this.imageBuffer = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();
        super.operate(grid, slopeGrid);
    }

    /**
     * Compute a chunk of the destination grid.
     *
     * @param src The source terrain elevation grid.
     * @param slopeGrid Slope grid.
     * @param startRow The index of the first row of the source grid.
     * @param endRow The index of the first row of the source grid.
     */
    @Override
    public void operate(Grid src, Grid slopeGrid, int startRow, int endRow) {
        startRow = Math.max(1, startRow);
        endRow = Math.min(src.getRows() - 2, endRow);
        int cols = src.getCols();
        
        int scale = image.getWidth() / src.getCols();
        if (scale == 1) {
            for (int row = startRow; row < endRow; row++) {
                for (int col = 1; col < cols - 1; col++) {
                    illuminatedContours(src, col, row);
                }
            }
        } else {
            // only report progress if this is the first chunk of the image
            // all chunks are the same size, but are rendered in different threads.
            boolean reportProgress = startRow == 1 
                    && progress instanceof SwingWorkerWithProgressIndicator;
            
            for (int row = startRow; row < endRow; row++) {
                // stop rendering if the user canceled
                if (progress != null && progress.isCancelled()) {
                    return;
                }

                // report progress made
                if (reportProgress) {
                    int percentage = Math.round(100f * row / (endRow - startRow));
                    ((SwingWorkerWithProgressIndicator) progress).progress(percentage);
                }

                // destination has different size than source grid.
                for (int col = 1; col < cols - 1; col++) {
                    scaledIlluminatedContours(src, col, row, slopeGrid, scale);
                }
            }
        }
    }

    /**
     * Compute a single grid value with illuminated contours that has the same
     * size as the source grid.
     *
     * @param src The source terrain elevation grid.
     * @param dst The destination grid with illuminated contour lines.
     * @param col The column in the source grid.
     * @param row The row in the source grid.
     */
    private void illuminatedContours(Grid src, int col, int row) {
        double slopeVal = src.getSlope(col, row);
        double aspectVal = (src.getAspect(col, row) + Math.PI) * 180 / Math.PI;
        double demVal = src.getValue(col, row);
        int g = computeGray(demVal, aspectVal, slopeVal, src.getCellSize());
        if (g != CONTOURS_TRANSPARENT) {
            int argb = (int) g | ((int) g << 8) | ((int) g << 16) | 0xFF000000;
            imageBuffer[row * image.getWidth() + col] = argb;
        }
    }

    /**
     * Compute a grid values corresponding to one cell in the source grid. The
     * destination grid has a different size than the source grid.
     *
     * @param src The source terrain elevation grid.
     * @param col The column in the source grid.
     * @param row The row in the source grid.
     * @param scale The image is this many times larger than the terrain model
     * grid.
     */
    private void scaledIlluminatedContours(Grid src, int col, int row,
            Grid slopeGrid, int scale) {
        final double cellSize = src.getCellSize();
        final double samplingDist = cellSize / scale;
        final double west = src.getWest();
        final double north = src.getNorth();

        // render scale x scale subcells in the destination grid
        for (int r = 0; r < scale; r++) {
            for (int c = 0; c < scale; c++) {
                // convert column and row to geographic coordinates
                double x = west + ((col + (double) c / scale) * cellSize);
                double y = north - ((row + (double) r / scale) * cellSize);

                // compute interpolated slope, aspect and elevation for subcells
                // the following code is equal to calling getSlope and getAspect,
                // but avoids interpolating the same altitude values twice.
                // double slopeVal = src.getSlope(x, y, samplingDist);
                // double aspectVal = (src.getAspect(x, y, samplingDist) + Math.PI) * 180 / Math.PI;
                double center = src.getBilinearInterpol(x, y);
                double w = src.getBilinearInterpol(x - samplingDist, y);
                double e = src.getBilinearInterpol(x + samplingDist, y);
                double s = src.getBilinearInterpol(x, y - samplingDist);
                double n = src.getBilinearInterpol(x, y + samplingDist);
                double dx = e - w;
                double dy = n - s;

                double slopeVal = slopeGrid.getBilinearInterpol(x, y);
                double aspectVal = Math.atan2(dy, dx);
                aspectVal = (aspectVal + Math.PI) * 180 / Math.PI;

                int g = computeGray(center, aspectVal, slopeVal, cellSize);
                if (g != CONTOURS_TRANSPARENT) {
                    int argb = (int) g | ((int) g << 8) | ((int) g << 16) | 0xFF000000;
                    int imageCol = col * scale + c;
                    int imageRow = row * scale + r;
                    imageBuffer[imageRow * image.getWidth() + imageCol] = argb;
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Illuminated Contours";
    }

    /**
     *
     * @param illuminationAzimuth angle of illumination from north in clockwise
     * direction
     * @param aspectAngle the aspect angle, from aspectGrid
     * @return the minimum angle between the two vectors
     */
    private double getAngleDifference(double illuminationAzimuth, double aspectAngle) {
        // convert azimuth angle to geometric angle, from east counterclockwise
        double geomAngle = 90 - illuminationAzimuth;
        double angleDifference;
        angleDifference = Math.abs((Math.abs(geomAngle - aspectAngle) + 180) % 360 - 180);
        return angleDifference;
    }

    /**
     * Compute the gray value for the illuminated contour line image
     *
     * @param elevation Elevation of the point.
     * @param aspect Aspect of the point.
     * @param slope Slope of the point.
     * @param cellSize
     * @return Gray value between 0 and 255.
     */
    public int computeGray(double elevation, double aspect, double slope, double cellSize) {

        // calculate minumum angle between illumination angle and aspect
        double angleDiff = getAngleDifference(azimuth, aspect);

        // set 'a' based on the angle distance from the angle of illumination.
        double a;
        if (angleDiff > 90) {
            a = shadowWidth * slope * cellSize;
        } else {
            a = illuminatedWidth * slope * cellSize;
        }
        if (tanaka) {
            a *= Math.abs(Math.cos(angleDiff / 180 * Math.PI));
        }

        // make lines minimum width
        a = Math.max(minWidth * slope * cellSize, a);

        // Code regarding 'a' vs 'diff' ensures that the contour lines
        // have an equal width throughout the illuminated or shadowed line.
        double dist = elevation % interval;
        if (dist > a) {
            dist = interval - dist;
        }

        if (a > dist) {
            if (angleDiff >= (90 + gradientAngle)) {
                return 0; // black
            } else if (angleDiff <= (90 - gradientAngle)) {
                return illuminated ? illluminatedGray : 0;
            } else if (angleDiff > (90 - gradientAngle) && angleDiff < (90 + gradientAngle)) {
                return (int) ((angleDiff - (90 - gradientAngle)) / ((90 + gradientAngle) - (90 - gradientAngle)) * (-255) + 255);
            }
        }
        return CONTOURS_TRANSPARENT;
    }
}