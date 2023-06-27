package de.host.mobsys.starrun.base.collision;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class IntersectionCalculator {
    public static Point[] computeIntersection(Point[] shape1, Point[] shape2) {
        // Clip shape1 against shape2
        Point[] result = clipPolygon(shape1, shape2);

        // Clip the resulting shape against shape1 to get the final intersection area
        result = clipPolygon(result, shape1);

        return result;
    }

    private static Point[] clipPolygon(Point[] polygon, Point[] clipper) {
        if (polygon.length == 0) {
            return new Point[0];
        }

        for (int i = 0; i < clipper.length; i++) {
            Point currentClipperPoint = clipper[i];
            Point nextClipperPoint = clipper[((i + 1) % clipper.length)];
            Point previousPoint = polygon[polygon.length - 1];

            List<Point> intersection = new ArrayList<>();
            for (Point currentPolygonPoint : polygon) {
                boolean isCurrentInside = isInsideClipperEdge(
                    currentPolygonPoint,
                    currentClipperPoint,
                    nextClipperPoint
                );

                if (isCurrentInside) {
                    if (!isInsideClipperEdge(
                        previousPoint,
                        currentClipperPoint,
                        nextClipperPoint
                    )) {
                        intersection.add(computeIntersectionPoint(
                            previousPoint,
                            currentPolygonPoint,
                            currentClipperPoint,
                            nextClipperPoint
                        ));
                    }

                    intersection.add(currentPolygonPoint);
                } else if (isInsideClipperEdge(
                    previousPoint,
                    currentClipperPoint,
                    nextClipperPoint
                )) {
                    intersection.add(computeIntersectionPoint(
                        previousPoint,
                        currentPolygonPoint,
                        currentClipperPoint,
                        nextClipperPoint
                    ));
                }

                previousPoint = currentPolygonPoint;
            }

            polygon = intersection.toArray(new Point[0]);
            if (polygon.length == 0) {
                break;
            }
        }

        return polygon;
    }

    private static boolean isInsideClipperEdge(Point point, Point clipperStart, Point clipperEnd) {
        return (clipperEnd.x - clipperStart.x) * (point.y - clipperStart.y)
               - (clipperEnd.y - clipperStart.y) * (point.x - clipperStart.x) >= 0;
    }

    private static Point computeIntersectionPoint(
        Point point1,
        Point point2,
        Point clipperStart,
        Point clipperEnd
    ) {
        double x1 = point1.x;
        double y1 = point1.y;
        double x2 = point2.x;
        double y2 = point2.y;
        double x3 = clipperStart.x;
        double y3 = clipperStart.y;
        double x4 = clipperEnd.x;
        double y4 = clipperEnd.y;

        double numeratorX = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4);
        double numeratorY = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4);
        double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        double x = numeratorX / denominator;
        double y = numeratorY / denominator;

        return new Point((int) Math.round(x), (int) Math.round(y));
    }
}

