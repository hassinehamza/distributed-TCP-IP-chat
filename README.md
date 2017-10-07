## distributed-TCP/IP-Chat

# Description :
This project is a realistic  distributed TCP/IP chat application. 

In this Project : 
- we uses  JAVA NIO library in order to read Messages from Network.
- we implement basic distributed algorithms : 
	- Election  : http://www-inf.it-sudparis.eu/COURS/AlgoRep/Web/5.7.html 
	- causal-order diffusion (based on Vector clock): http://www-inf.it-sudparis.eu/COURS/AlgoRep/Web/6.11.13.html
 
# How to Try : 
1 - Install Apache Maven See http://maven.apache.org/download.html#Installation.

2 - `cd Sources`	

3 - run `mvn clean install`

4 - to run a server : `./serveur.sh <server number> <list of pairs hostname servernumber>`

5 - to run a client :  `./client <machine> <port>`

	
__we will try this topology:__ 

PS: when starting a server <i> , its default port will be 2050+i;
in order to conect a server to a server , we should mention the host and the Id of the server.
in order to connect a client to a server , we have to mention the server host and port . 


./serveur.sh 1
./client.sh localhost 2051
./serveur 2 localhost 1
./client.sh localhost 2052
./serveur 3 localhost 1
./client.sh localhost 2053
./client.sh localhost 2053

```
client--------server(1)------server(2)------client 
                |
                |
 client----server(3)-----client
```

