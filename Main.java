import java.util.*;
class FlowEdge {
    private final int from;
    private final int to;
    private double capacity;
    private double flow;

    public FlowEdge(int from, int to, double capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0.0;
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public double capacity() {
        return capacity;
    }

    public double flow() {
        return flow;
    }

    public int other(int vertex) {
        if (vertex == from) return to;
        else if (vertex == to) return from;
        else throw new IllegalArgumentException("Invalid vertex");
    }

    public double residualCapacityTo(int vertex) {
        if (vertex == from) return flow;
        else if (vertex == to) return capacity - flow;
        else throw new IllegalArgumentException("Invalid vertex");
    }

    public void addResidualFlowTo(int vertex, double delta) {
        if (vertex == from) flow -= delta;
        else if (vertex == to) flow += delta;
        else throw new IllegalArgumentException("Invalid vertex");
    }
}
class FlowNetwork {
    private final int V;
    private List<FlowEdge>[] adj;

    public FlowNetwork(int V) {
        this.V = V;
        adj = (ArrayList<FlowEdge>[]) new ArrayList[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new ArrayList<>();
        }
    }

    public int V() {
        return V;
    }

    public void addEdge(FlowEdge edge) {
        int from = edge.from();
        int to = edge.to();
        adj[from].add(edge);
        adj[to].add(edge);
    }

    public Iterable<FlowEdge> adj(int v) {
        return adj[v];
    }
}
class FordFulkerson {
    private boolean[] marked;
    private FlowEdge[] edgeTo;
    private double value;

    public FordFulkerson(FlowNetwork G, int s, int t) {
        value = 0.0;
        while (hasAugmentingPath(G, s, t)) {
            double bottleneck = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottleneck = Math.min(bottleneck, edgeTo[v].residualCapacityTo(v));
            }
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottleneck);
            }
            value += bottleneck;
        }
    }

    public double value() {
        return value;
    }

    public boolean inCut(int v) {
        return marked[v];
    }

    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
        marked = new boolean[G.V()];
        edgeTo = new FlowEdge[G.V()];

        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        marked[s] = true;

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (FlowEdge edge : G.adj(v)) {
                int w = edge.other(v);
                if (!marked[w] && edge.residualCapacityTo(w) > 0) {
                    edgeTo[w] = edge;
                    marked[w] = true;
                    queue.add(w);
                }
            }
        }
        return marked[t];
    }
}
public class Main {
    public static void main(String[] args) {
        // Define the number of nodes in the flow network
        int numNodes = 6;
        
        // Create a flow network with the specified number of nodes
        FlowNetwork network = new FlowNetwork(numNodes);

        // Define the source and sink nodes
        int source = 0;
        int sink = numNodes - 1;

        // Add edges to the flow network with their capacities
        network.addEdge(new FlowEdge(0, 1, 10.0));
        network.addEdge(new FlowEdge(0, 2, 5.0));
        network.addEdge(new FlowEdge(1, 3, 15.0));
        network.addEdge(new FlowEdge(1, 4, 10.0));
        network.addEdge(new FlowEdge(2, 4, 15.0));
        network.addEdge(new FlowEdge(3, 5, 10.0));
        network.addEdge(new FlowEdge(4, 5, 10.0));

        // Calculate the maximum flow using the Ford-Fulkerson algorithm
        FordFulkerson fordFulkerson = new FordFulkerson(network, source, sink);
        double maxFlow = fordFulkerson.value();

        // Output the maximum flow
        System.out.println("Maximum Flow: " + maxFlow);
    }
}

