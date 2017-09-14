# Scriptwars
#
# ∩༼˵☯‿☯˵༽つ¤=[]:::::>

Author: Sidharth Mishra << sidmishraw@gmail.com >>



## Motivation(?)

Just for Fun! Jk
To check out the ways we can stream/transport compute in distributed/multi-agent networks (?) 

Personally, I use it for fun lol!

This application simulates a way of transporting compute over the network. The scriptwar consists of a HTTP webserver.

The game has 2 contexts `/attack` and `/receive`.


	**AND**


The server has 2 contexts `/serialize` and `/deserialize`.


## `/attack`
The `/attack` context is used to submit the attack to the opponent via a POST request. It takes the `host` and `port` of the agent to attack as `x-www-form-urlencoded` parameters. The attack's damage is computed from the js script used while starting the game!


## `/receive`
The `/receive` context is used to receive the attack from the opponent via a POST request. If the your HP drops below `0`, your game ends and the application ends.

## JS Script template

The js file named warscript is the template that needs to be modified. It needs the `computeDamage` function. Please do not modify the function signature. The body of the function is actually what needs to be modified to do the damage.

Have fun!


## Samples:

Agent#1
```
sidmishraw@Sidharths-MBP ~/Desktop> java -jar scriptwars.jar localhost 3030 warscript.js 
Host:: localhost :: Port :: 3030



Receiving attack from :: /receive
Computing damage!
Damage received:: 100.0
Your HP:: 270.7929315264443
Computing the attack
Attacking agent at :: localhost:3050
Initiating attack...
Logging:: Attack object :: StandardLambdaObject [objectId=Sid#0001, lambdaJSScript=/** * `computeDamage` is the function that specifies the damage to be dealt to the * opponent. The `baseDamage` and `randomMultiplier` are mandatory and are set * randomly when the script executes. *  * To print stuff or display messages, you can use `println()` or `print()` *  * @param baseDamage *            the baseDamage of the attack, a double * @param randomMultiplier *            the random multiplier that can be used in the attack, a double *  * @return the computed damage, a double */var computeDamage = function (baseDamage, randomMultiplier) {	print("Computing damage!");	return 150.0;};]
Awaiting response...
Attack stats :: Attack succeeded, damage dealt :: 150.0
```

Agent#2
```
sidmishraw@Sidharths-MBP ~/Desktop> java -jar scriptwars.jar localhost 3050 warscript2.js 
Host:: localhost :: Port :: 3050

Computing the attack
Attacking agent at :: localhost:3030
Initiating attack...
Logging:: Attack object :: StandardLambdaObject [objectId=Sid#0001, lambdaJSScript=/** * `computeDamage` is the function that specifies the damage to be dealt to the * opponent. The `baseDamage` and `randomMultiplier` are mandatory and are set * randomly when the script executes. *  * To print stuff or display messages, you can use `println()` or `print()` *  * @param baseDamage *            the baseDamage of the attack, a double * @param randomMultiplier *            the random multiplier that can be used in the attack, a double *  * @return the computed damage, a double */var computeDamage = function (baseDamage, randomMultiplier) {	print("Computing damage!");	return 100.0;};]
Awaiting response...
Attack stats :: Attack succeeded, damage dealt :: 100.0
Receiving attack from :: /receive
Computing damage!
Damage received:: 150.0
Your HP:: 643.106388567504
```



## For Simulations of transporting compute::
### `/serialize`
The `/serialize` context takes in a POST request with 2 form-url-encoded params "host" and "port". These are the hostname and port of the next agent/java process running the scriptwar application(jar).

This context/API endpoint, will create the object and assign the JS script contents(lambda replacement) into the object and serialize and wire it to the agent with the `host` and `port` provided. It then waits till the receiving agent replies back.


### `/deserialize`
The `/deserialize` context receives the serialized object wired to it, and executes the JS script(lamda replacement) and then returns a success response if everything went well else does it's usual thingy :D


## Usage:

For the simulation, start up 2 separate java processes like

Agent#1
```
java -jar scriptwars.jar localhost 3030 lamda.js
```

Agent#2
```
java -jar scriptwars.jar localhost 3050 l2.js
```

Then using Postman client, hit the URL "http://localhost:3030/serialize" using a POST request and "host=localhost", "port=3050" as form-url-encoded params.

It will execute the contents of the script `lamda.js` at Agent#2 and vice versa.





## Generic usage
```
java -jar scriptwars.jar <my host-name> <my port nbr> <my js script file path>
```


 - Sid
