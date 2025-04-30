package cs113.trolley;

import javafx.scene.paint.Color;

import java.util.*;

// ********** Graph Construction ********** //
class TrolleyGraph {
    private List<TrolleyStation> stations;
    private List<TrolleyRoute> routes;

    public TrolleyGraph() {
        stations = new ArrayList<>();
        routes = new ArrayList<>();
    }

    // Add a new station (node) to the graph
    public void addStation(String name, int x, int y) {
        // todo: Implement this method to add a new station
        // Make sure to check if a station with the same name already exists

//        TrolleyStation station = new TrolleyStation(name, x, y);
//        if (!stations.contains(station)) {
//            stations.add(station);
//        }
        if(getStationByName(name) == null){
            TrolleyStation station = new TrolleyStation(name, x, y);
            stations.add(station);
        }
    }

    // Get a station by its name
    public TrolleyStation getStationByName(String name) {
        // todo: Implement this method to find a station by name
//        Iterator<TrolleyStation> iterator = stations.iterator();
//        while (iterator.hasNext()) {
//            TrolleyStation station = iterator.next();
//            if (station.getName().equals(name)) {
//                return station;
//            }
//        }

        for (TrolleyStation station : stations) {
            if (station.getName().equals(name)) return station;
        }
        return null;
    }

    // Get all station names
    public Set<String> getStationNames() {
        Set<String> names = new HashSet<>();
        for (TrolleyStation station : stations) {
            names.add(station.getName());
        }
        return names;
    }

    // Add a new route (edge) between two stations
    public void addRoute(String fromStation, String toStation, int weight, Color color) {
        // todo: Implement this method to add a new route
        // Make sure both stations exist before adding the route

        if(getStationByName(fromStation) == null) return;
        if(getStationByName(toStation) == null) return;

        TrolleyRoute route = new TrolleyRoute(fromStation, toStation, weight, color);
        routes.add(route);
        route = new TrolleyRoute(toStation, fromStation, weight, color);
        routes.add(route);
    }

    // Get all stations
    public List<TrolleyStation> getStations() {
        return stations;
    }

    // Get all routes
    public List<TrolleyRoute> getRoutes() {
        return routes;
    }

    // ********** Adjacency Lists ********** //
    public List<String> getAdjacentStations(String stationName) {
        // todo: Implement this method to find all stations connected to the given station
        List<String> adjacent = new ArrayList<>();

        for (TrolleyRoute  route : routes) {
            if (route.getFromStation().equals(stationName)) {
                adjacent.add(route.getToStation());
            }
        }
        return adjacent;
    }

    // Get the weight of a route between two stations
    public int getRouteWeight(String fromStation, String toStation) {
        // todo: Calculate the route weight between stations
        for (TrolleyRoute  route : routes) {
            if(route.getFromStation().equals(fromStation)
            && route.getToStation().equals(toStation)) {
                return route.getWeight();
            }
        }
        return -1; // No direct route
    }

    // ********** Breadth First Search (BFS) ********** //
    public List<String> breadthFirstSearch(String startStation, String endStation) {
        // todo: Implement a BFS (see readme)
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        queue.add(startStation);
        while(!queue.isEmpty()) {
            String current = queue.poll();
            if(current.equals(endStation)) {
                return reconstructPath(parentMap, startStation, endStation);
            }
            visited.add(current);

            List<String> neighbors = getAdjacentStations(current);
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        return null; // No path found
    }

    // ********** Depth First Search (DFS) ********** //
    public List<String> depthFirstSearch(String startStation, String endStation) {
        // todo: Implement a DFS (see readme)
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        if( dfsHelper(parentMap, visited, startStation, endStation) != null) {
            return reconstructPath(parentMap, startStation, endStation);
        }
        return null; // No path found
    }

    Map<String, String> dfsHelper(Map<String, String> parentMap, Set<String> visited, String current, String end) {
       if (current.equals(end) ) return parentMap;

       visited.add(current);

           List<String> neighbors = getAdjacentStations(current);
           for (String neighbor : neighbors) {
               if (!visited.contains(neighbor)) {
                   parentMap.put(neighbor, current);
                   visited.add(neighbor);
                   if (dfsHelper(parentMap, visited, neighbor, end) != null) {
                       return parentMap;
                   }
               }
           }
           return null;
    }

    // ********** Dijkstra's Algorithm ********** //
    public List<String> dijkstra(String startStation, String endStation) {
        // todo: Implement Dijkstra's Algorithm
        Map<String, String> parentMap = new HashMap<>();
        int[] distances = new int[stations.size()];
        for (int i = 0; i < distances.length ; i++) {
            distances[i] = Integer.MAX_VALUE;
        }
        PriorityQueue<DNode> pq = new PriorityQueue<>();
        pq.add(new DNode(startStation, 0));
        distances[index(startStation)] = 0;

        while (!pq.isEmpty()) {
            DNode node = pq.poll();
            if(node.name.equals(endStation)) {
                return reconstructPath(parentMap, startStation, endStation);
            }
            int distance = distances[index(node.name)];

            List<String> neighbors = getAdjacentStations(node.name);
            for (String neighbor : neighbors) {
                int neighbor_index = index(neighbor);
                int old_distance = distances[index(neighbor)];
                int weight = getRouteWeight(node.name, neighbor);

                if(old_distance > distance+weight) {
                    //found shorter to get there
                    parentMap.put(neighbor, node.name);
                    distances[neighbor_index] = distance+weight;
                    DNode nnode = new DNode(neighbor, distances[neighbor_index]);
                    pq.add(nnode);
                }
            }
        }


        return null; // No path found
    }

    int index(String name) {
        TrolleyStation station = getStationByName(name);
        return stations.indexOf(station);
    }

    class DNode implements Comparable<DNode> {

        String name;
        int distance;

        DNode(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }

        @Override
        public int compareTo(DNode o) {
            return this.distance - o.distance;
        }
    }

    // Helper method to reconstruct the path from start to end using the parent map
    private List<String> reconstructPath(Map<String, String> parentMap, String start, String end) {
        List<String> path = new ArrayList<>();
        String current = end;

        while (current != null) {
            path.add(0, current);
            current = parentMap.get(current);

            if (current != null && current.equals(start)) {
                path.add(0, start);
                break;
            }
        }

        return path.size() > 1 ? path : null;
    }
}