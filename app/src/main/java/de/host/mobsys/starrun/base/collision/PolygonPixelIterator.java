package de.host.mobsys.starrun.base.collision;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to iterate over every pixel of any polygon
 */
public class PolygonPixelIterator {

    /**
     * Iterates over every pixel of a polygon and calls a callback
     *
     * @param corners  Corner points of the polygon
     * @param callback Called on every pixel. Return true to stop the iteration process
     */
    public void iteratePolygonPixels(Point[] corners, PixelCallback callback) {
        List<Edge> edges = createEdges(corners);

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Point corner : corners) {
            minY = Math.min(minY, corner.y);
            maxY = Math.max(maxY, corner.y);
        }

        for (int y = minY; y <= maxY; y++) {
            List<Integer> intersections = createIntersections(edges, y);
            for (int i = 0; i < intersections.size() - 1; i += 2) {
                int startX = intersections.get(i);
                int endX = intersections.get(i + 1);

                for (int x = startX; x <= endX; x++) {
                    if (callback.onPixel(x, y)) {
                        return;
                    }
                }
            }
        }
    }

    private List<Edge> createEdges(Point[] corners) {
        List<Edge> edges = new ArrayList<>();
        int numCorners = corners.length;
        for (int i = 0; i < numCorners; i++) {
            Point p1 = corners[i];
            Point p2 = corners[(i + 1) % numCorners];
            edges.add(new Edge(p1, p2));
        }

        return edges;
    }

    private List<Integer> createIntersections(List<Edge> edges, int y) {
        List<Integer> intersections = new ArrayList<>();
        for (Edge edge : edges) {
            int xIntersection = edge.findIntersectionX(y);
            if (xIntersection != Integer.MIN_VALUE) {
                intersections.add(xIntersection);
            }
        }

        intersections.sort(Integer::compareTo);

        return intersections;
    }

    @FunctionalInterface
    public interface PixelCallback {
        /**
         * Called on every pixel of a polygon
         *
         * @return true when iteration should be finished, false otherwise
         */
        boolean onPixel(int x, int y);
    }

    private static class Edge {
        Point p1;
        Point p2;

        public Edge(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        public int findIntersectionX(int y) {
            if (p1.y == p2.y || y < Math.min(p1.y, p2.y) || y > Math.max(p1.y, p2.y)) {
                return Integer.MIN_VALUE; // No intersection
            }

            int dx = p2.x - p1.x;
            int dy = p2.y - p1.y;

            return p1.x + (int) ((double) dx / dy * (y - p1.y));
        }
    }
}
