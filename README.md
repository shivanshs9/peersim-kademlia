# Kademlia DHT for the Peersim Simulator

### What is it?
Kademlia is a DHT protocol which offers a number of desirable features not simultaneously offered by any other DHT. It makes sure that there are minimum lookup
messages sent to locate a node. The algorithm resists various denial-of-service attacks. Each node is associated with a 160-bit long node ID via which each node is
located. The distance between any two nodes with ID’s x and y is given by x XOR y.
The lookup algorithm in kademlia successfully located closest nodes to any node-ID,
converging to the target in logarithmic steps.

#### RPCs provided by Kademlia
The Kademlia protocol consists of 4 RPC’s:
1. **PING** checks if a particular node is online.
2. **STORE** instructs a node to store a <key,value> pair for later retrieval
3. **FIND NODE** takes a 160-bit node ID as parameter and returns the k-closest nodes it thinks are the nearest to the node.
4. **FIND VALUE** takes a node-ID as well as key as parameters. If there has been a STORE operation on that key on the parameter node then it returns that stored value, else it works exactly the same as FIND NODE.

### How to use this?

An example example.cfg configuration file can be found in the  main directory.

#### Makefile

1. To compile the sources, invoke:
```
  make
```
2. To compile the API documentation, invoke:
```
  make doc
```
3. To run the code, invoke:
```
  make run
```
4. To run all the previous command in this order, invoke:
```
  make all
```
