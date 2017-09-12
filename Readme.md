# Scriptwars


Author: Sidharth Mishra<sidmishraw@gmail.com>



##Motivation(?)

Just for Fun! Jk
To check out the ways we can stream/transport compute in distributed/multi-agent networks (?) 

Personally, I use it for fun lol!

This application simulates a way of transporting compute over the network. The scriptwar consists of a HTTP webserver.
The server has 2 contexts `/serialize` and `/deserialize`.


## `/serialize`
The `/serialize` context takes in a POST request with 2 form-url-encoded params "host" and "port". These are the hostname and port of the next agent/java process running the scriptwar application(jar).

This context/API endpoint, will create the object and assign the JS script contents(lambda replacement) into the object and serialize and wire it to the agent with the `host` and `port` provided. It then waits till the receiving agent replies back.


## `/deserialize`
The `/deserialize` context receives the serialized object wired to it, and executes the JS script(lamda replacement) and then returns a success response if everything went well else does it's usual thingy :D


## Usage:

For the simulation, start up 2 separate java processes like

Agent#1
```
java -jar scriptwar.jar localhost 3030 lamda.js
```

Agent#2
```
java -jar scriptwar.jar localhost 3050 l2.js
```

Then using Postman client, hit the URL "http://localhost:3030/serialize" using a POST request and "host=localhost", "port=3050" as form-url-encoded params.

It will execute the contents of the script `lamda.js` at Agent#2 and vice versa.





## Generic usage
```
java -jar scriptwar.jar <my host-name> <my port nbr> <my js script file path>
```


 - Sid
