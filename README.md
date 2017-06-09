# ProtocoloAutenticacionWMN

## Descripción

El presente proyecto corresponde a la implementación del protocolo de autenticación especificado en la memoria: "Protocolo de autenticación para la aplicación de una red inalámbrica de malla", la cual consta de tres softwares independendientes que simulan el comportamiento, por un lado de la red, y por otro, el de los dos tipos de nodos conectados a la red, los MRs y los MCs.

El prototipo esta escrito en el lenguaje Java, haciendo uso de la herramienta JavaRMI para la comunicación entre las ejecuciones de los tres programas, además, se hace uso de una libreria externa JPBC usada para ciertas tareas criptográficas.

### WMN

En WMN se simula el comportamiento de una Wireless Mesh Network bajo las condiciones establecidas en la memoria. Su ejecución permite la conexión entre los otros dos programas, funcionando como el servidor en la infraestructura de comunicación planteada por javaRMI.

### MR

En MR se simula el comportamiento de un conjunto de mesh routers, definiendose al inicio de la ejecución, la cantidad de estos que se quieren en la red simulada. Por lo tanto, es necesario solo una ejecución de este programa para llevar a cabo la simulación del protocolo.

### MC

En MC se simula a cada uno de los mesh client que desean conectarse a la red, llevando a cabo los pasos establecidos en al memoria para cada cliente que se desea autenticar, a excepción de los primeros mesh clients, los cuales se autentican automáticamente uno a uno con los routers hasta que exista la misma cantidad entre estos.

## Ejecución

# Librerias usadas:

- JPBC : http://gas.dia.unisa.it/projects/jpbc/#.WTs4oes1_IU
