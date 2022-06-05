#!/bin/bash

echo "Starting copy..."
cd ..
scp -r callbackIce/ swarch@xhgrid13:/home/swarch/a00362288/final/
ssh swarch@xhgrid13
cd /home/swarch/a00362288/final/