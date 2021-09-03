# Project from MTS.Teta Summer School Hackaton
## Case
My team had to develop the application which classifies wheather given web-address is technical (smth like api.google.bla.bla.com) or it's for user (youtube.com for example). 

## What is that?
Shortly, there are 2 server applications run concurently on same machine:
* Python Flask application with ML model (written by my teammates) - it classifies given web-address.
* Java Spring application (written by me) - prototype for final presentation of model (web-site).

## How it works?
1. User enters web-address to form on my site
2. This address passes simple javascript-validation
3. Java-server gets http-request with given address and validate address string for the second time
4. Python-server gets http-request with address from Java-server and classifies it
5. Java-server gets response with classification result from Python-server and send it to user

## Features
We added handling of .csv files. User gives .csv with list of web-addresses and gets .csv with answers.
