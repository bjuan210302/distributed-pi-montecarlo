#pragma once

module Montecarlo
{
    class Task {
        long target;
        int epsilonExponent;
        long seed;
        long seedOffset;
    };

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
        void unsubscribe(Worker* observer);
    };

    interface Master extends Subject
    {
        Task getTask();
        void reportPartialResult(long inside, long outside);
    };

}