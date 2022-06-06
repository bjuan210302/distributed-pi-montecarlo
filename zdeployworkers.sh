#!/bin/bash

cd ..

echo "Starting copy 10"
sed -i '11s/.*/Worker.Endpoints=default -h hgrid10 -p 9002/' callbackIce/worker/src/main/resources/config.client
sshpass -p "swarch" scp -r callbackIce/ swarch@xhgrid10:/home/swarch/a00362288/final/

echo "Starting copy 11"
sed -i '11s/.*/Worker.Endpoints=default -h hgrid11 -p 9002/' callbackIce/worker/src/main/resources/config.client
sshpass -p "swarch" scp -r callbackIce/ swarch@xhgrid11:/home/swarch/a00362288/final/

echo "Starting copy 12"
sed -i '11s/.*/Worker.Endpoints=default -h hgrid12 -p 9002/' callbackIce/worker/src/main/resources/config.client
sshpass -p "swarch" scp -r callbackIce/ swarch@xhgrid12:/home/swarch/a00362288/final/

echo "Starting copy 13"
sed -i '11s/.*/Worker.Endpoints=default -h hgrid13 -p 9002/' callbackIce/worker/src/main/resources/config.client
sshpass -p "swarch" scp -r callbackIce/ swarch@xhgrid13:/home/swarch/a00362288/final/
