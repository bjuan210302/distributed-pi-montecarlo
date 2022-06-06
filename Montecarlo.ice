#pragma once

module Montecarlo
{
    class Point {
        double x;
        double y;
        bool isInside;
    };

    ["java:type:java.util.LinkedList<Point>:java.util.LinkedList<Point>"]
    sequence<Point> OrderedPointList;

    class Task {
        long target;
        int epsilonExponent;
        long seed;
        long seedOffset;
    };

    sequence<Point> PointSequence;

    interface Observer
    {
        void update(bool taskAvailable);
    };

    interface Worker extends Observer
    {
    };

    interface Subject
    {
        void subscribe(Worker* observer);
    };

    interface Master extends Subject
    {
        Task getTask();
        void reportPartialResult(OrderedPointList points);
    };

}