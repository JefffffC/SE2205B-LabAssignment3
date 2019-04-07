public class Assignment3{

    private int[] augmentingPath; // global array for storing augmenting path

    public int breadthFirstPathSearch(Graph FN, int s, int d) { // I assume FN is an UNDIRECTED graph, hence has two directional edges for every edge

        augmentingPath = new int [FN.numVertices()];// set appropriate array length
        for (int i = 0; i < augmentingPath.length; i++) {
            augmentingPath[i] = -1; // set every element in augmentingPath array to -1 to avoid confusion with label of source node which is 0
        }
        int [] visitedNodes = new int[FN.numVertices()];
        for (int i = 0; i < visitedNodes.length; i++) { // set every element in visitedNodes array to
            visitedNodes[i] = 0;
        }
        LinkedListQueue<Vertex<V>> storageQueue = new LinkedListQueue<Vertex<V>>();
        Vertex<V> startingNode;
        for (Vertex<V> v : FN.vertices()) { // get an iterator for Graph FN to find the right vertex
            if (v.getLabel() == s) { // I assume that Vertex has interface getLabel() which represents its number position in Graph
                startingNode = v; // set starting node to correct
                break;
            }
            else {
                startingNode = null;
                System.out.println("Could not find node with index given!");
                return 0; // break and return 0 since starting node could not be found in graph
            }
        }
        storageQueue.enqueue(startingNode);
        Vertex<V> parentNode;
        Vertex<V> adjacentNode;
        while (!storageQueue.isEmpty()) { // once queue is empty, the breadth-first will be done and augmentingPath will have shortest viable path
            parentNode = storageQueue.dequeue();
            for (Edge<E> e : FN.outgoingEdges(parentNode)) { // go down each path going away from current node
                adjacentNode = FN.opposite(parentNode, e); // get the nodes adjacent to current node
                if ((visitedNodes[adjacentNode.getLabel()] != 1) // check if adjacent node is unvisited
                        && ((e.flowCapacity() - e.flow()) > 0)) { // check if current flow to adjacent has not reached max
                    augmentingPath[adjacentNode.getLabel()] = parentNode.getLabel(); // index corresponding to adjacent node is set to parent
                    visitedNodes[adjacentNode.getLabel()] = 1; // mark node as visited in index corresponding to adjacent node
                    storageQueue.enqueue(adjacentNode); // enqueue discovered node into queue for breadth-first search
                }
            }
        }
        if (visitedNodes[d] == 0) return 0; // return 0 if destination node is unvisited
        else return 1; // otherwise return 1 since it is visited
    }

    public void maximizeFlowNetwork(Graph FN, int s, int t) {
        int maximumNetworkFlow = 0; // storage value for maximum overall network flow
        while (breadthFirstPathSearch(FN, s, t) != 0) { // loop repeatedly calls until there is no longer a possible augmenting path
            int augmentingIndex = FN.numVertices() - 1; // reverse path traversal starts at last index of augmenting path array
            Vertex<V> childNode; // reverse path traversal starts at terminal
            Vertex<V> parentNode; // reverse path traversal's initial parent node is the last index of augmenting path array
            // together, parentNode ---> childNode is first edge to be reverse traversed in this algorithm
            for (Vertex<V> v : FN.vertices()) { // get an iterator for Graph FN to find the required nodes
                if (v.getLabel() == t) {
                    childNode = v; // set initial childNode to terminal node of graph
                }
                if (v.getLabel() == augmentingPath[augmentingIndex]) {
                    parentNode = v; // set initial parentNode to last index of augmenting path array
                }
            }
            int maxAddableFlow; // storage for maximum possible flow to be added to path
            Edge<E> targetEdge; // storage for edge in traversal
            targetEdge = FN.getEdge(parentNode, childNode); // first edge is found
            maxAddableFlow = targetEdge.flowCapacity() - targetEdge.flow(); //maxAddableFlow is initialized
            while (true) { // this reverse path traversal terminates when it reaches the source node using a break line
                --augmentingIndex; // move to next index in augmenting path array, going backwards
                childNode = parentNode; // former start node becomes new end node of edge in reverse traversal
                for (Vertex<V> v : FN.vertices()) { // get an iterator for Graph FN to find the required node
                    if (v.getLabel() == augmentingPath[augmentingIndex]) {
                        parentNode = v; // find and set the correct vertex to parent node according to augmenting path
                    }
                }
                targetEdge = FN.getEdge(parentNode, childNode); // new edge in reverse traversal is found
                if ((targetEdge.flowCapacity() - targetEdge.flow()) < maxAddableFlow) { // check if it is a smaller flow than existing max
                    maxAddableFlow = targetEdge.flowCapaciy() - targetEdge.flow(); // if it is, then new max is set
                }
                if ((parentNode.getLabel() == s) || (augmentingIndex == s)) {
                    break; // once the edge that goes from source node is calculated, the depth reverse traversal is complete, break loop
                }
            }
            // once the loop terminates, then the maximum amount of flow to be added to each edge in the augmenting path has been found
            augmentingIndex = FN.numVertices() -1; // reset augmentingIndex for another reverse traversal
            // the loop below uses the iterator to also reset the childNode and parentNode for another reverse traversal
            for (Vertex<V> v : FN.vertices()) { // get an iterator for Graph FN to find the required nodes
                if (v.getLabel() == t) {
                    childNode = v; // set initial childNode to terminal node of graph
                }
                if (v.getLabel() == augmentingPath[augmentingIndex]) {
                    parentNode = v; // set initial parentNode to last index of augmenting path array
                }
            }
            targetEdge = FN.getEdge(parentNode, childNode); // first edge is found once again
            targetEdge.flow = targetEdge.flow + maxAddableFlow; // max addable flow is added to target edge
            targetEdge = FN.getEdge(childNode, parentNode); // reverse direction edge is found for backflow
            targetEdge.flow = targetEdge.flow - maxAddableFlow; // I assume backflow is subtracted for reverse direction as in negative flow
            /*
            the following reverse path traversal loop is same as the last loop,
            but this time adds computed max flow to all edges in the augmenting path and accounts for backflow
             */
            while (true) {
                --augmentingIndex; // move to next index in augmenting path array, going backwards
                childNode = parentNode; // former start node becomes new end node of edge in reverse traversal
                for (Vertex<V> v : FN.vertices()) { // get an iterator for Graph FN to find the required node
                    if (v.getLabel() == augmentingPath[augmentingIndex]) {
                        parentNode = v; // find and set the correct vertex to parent node according to augmenting path
                    }
                }
                targetEdge = FN.getEdge(parentNode, childNode);
                targetEdge.flow = targetEdge.flow + maxAddableFlow;
                targetEdge = FN.getEdge(childNode, parentNode); // reverse direction edge is found for backflow
                targetEdge.flow = targetEdge.flow - maxAddableFlow; // same assumption as before, negative flow for backflow, standard convention
                maximumNetworkFlow = maximumNetworkFlow + maxAddableFlow;
                if ((parentNode.getLabel() == s) || (augmentingIndex == s)) {
                    break; // once the edge that goes from source node is updated, the depth reverse traversal is complete, break loop
                }
            }
        }
        //before function terminates, it will print the max flow for the network, which is the sum of the bottleneck values for each iteration
        System.out.println("The maximum flow for the network from source to terminal nodes is " + maximumNetworkFlow + "."); // print maximum flow
    }
}






