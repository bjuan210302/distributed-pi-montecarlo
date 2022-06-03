//
// Copyright (c) ZeroC, Inc. All rights reserved.
//

#pragma once

module Montecarlo
{
    class Point {
        double x;
        double y;
    };

    class Task {
        int numberOfPointsToGenerate;
        int epsilonExponent;
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
        void reportPartialResult(PointSequence outside, PointSequence inside);
    };

}