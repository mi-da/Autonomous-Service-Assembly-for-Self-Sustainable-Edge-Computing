# Autonomous Service Assembly for Self-Sustainable Edge Computing
This repository contains the simulation code to replicate the results of the experimantation of the paper "Autonomous Service Assembly for Self-Sustainable Edge Computing" .

### Instructions
1. Download the Java Project "Energy-Aware-Service-Assembly" and import it in your IDE as a Java project
2. Link the provided libraries in "ext-lib" to the project
3. Input the program argument "configs/mida-assembly-config.txt" (i.e., the configuration file of PeerSim)
4. The main class to run the experiments is "peersim.Simulator"

### Configuration Parameters
The file configs/mida-assembly-config.txt contains the configuration parameters for the simulation. The main parameters are:
- NETWORK_SIZE: The number of network nodes.
- SERVICES_PER_NODE: The number services per node.
- TYPES: Number of service types.
- M: learning window of the algorithm .
- STRATEGY: The selection criteria that the services adopt.
  
### References 
[1] M. D’Angelo, M. Caporuscio, V. Grassi, and R. Mirandola, ‘Decentralized learning for self-adaptive QoS-aware service assembly’, Future generations computer systems, vol. 108, pp. 210–227, 2020. https://doi.org/10.1016/j.future.2020.02.027.
[2] A. Schaerf, Y. Shoham, and M. Tennenholtz. 1995. Adaptive load balancing: a study in multi-agent learning. J. Artif. Int. Res. 2, 1 (August 1994), 475–500.

### Contact 
If you have additional questions do not hesitate to contact me: https://lnu.se/en/staff/mirko.dangelo/
