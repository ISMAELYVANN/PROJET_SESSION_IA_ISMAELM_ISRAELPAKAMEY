package mas.graphs;

        import com.github.rinde.rinsim.geom.ConnectionData;
        import com.github.rinde.rinsim.geom.Graph;
        import com.github.rinde.rinsim.geom.Point;
        import java.util.HashMap;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.Map;

public class BellmanFord {

    public static List<Point> getShortestPath(Graph<? extends ConnectionData> graph, Point start, List<Point> dest) {
        List<Point> fullPath = new LinkedList<>();
        Point currentStart = start;

        for (Point destination : dest) {
            List<Point> pathSegment = getSingleDestinationPath(graph, currentStart, destination);
            if (pathSegment == null) {
                return null; // Si un chemin n'est pas trouvé, retourner null
            }

            // Ajouter le segment de chemin au chemin complet, en évitant de dupliquer le point de départ pour les segments successifs
            if (fullPath.isEmpty()) {
                fullPath.addAll(pathSegment);
            } else {
                fullPath.addAll(pathSegment.subList(1, pathSegment.size()));
            }

            currentStart = destination; // Mettre à jour le point de départ pour le prochain segment
        }

        return fullPath;
    }


    private static List<Point> getSingleDestinationPath(Graph<? extends ConnectionData> graph, Point start, Point dest) {
        Map<Point, Double> distance = new HashMap<>();
        Map<Point, Point> predecessor = new HashMap<>();

        // Initialisation
        for (Point p : graph.getNodes()) {
            distance.put(p, Double.MAX_VALUE);
            predecessor.put(p, null);
        }
        distance.put(start, 0.0);

        // Relaxation des arêtes
        for (int i = 0; i < graph.getNodes().size() - 1; i++) {
            for (Point u : graph.getNodes()) {
                for (Point v : graph.getOutgoingConnections(u)) {
                    double weight = dist_between(u, v); // Utilisation de dist_between pour calculer le poids
                    if (distance.get(u) + weight < distance.get(v)) {
                        distance.put(v, distance.get(u) + weight);
                        predecessor.put(v, u);
                    }
                }
            }
        }

        // Vérification des cycles de poids négatif
        for (Point u : graph.getNodes()) {
            for (Point v : graph.getOutgoingConnections(u)) {
                double weight = dist_between(u, v);
                if (distance.get(u) + weight < distance.get(v)) {
                    System.out.println("Graph contains a negative-weight cycle");
                    return null;
                }
            }
        }

        // Reconstruction du chemin le plus court
        return reconstructPath(predecessor, dest);
    }

    private static List<Point> reconstructPath(Map<Point, Point> predecessor, Point dest) {
        LinkedList<Point> path = new LinkedList<>();
        for (Point at = dest; at != null; at = predecessor.get(at)) {
            path.addFirst(at);
        }
        return path;
    }

    private static Double dist_between(Point current, Point neighbor) {
        return Math.abs(current.x - neighbor.x) + Math.abs(current.y - neighbor.y);
    }
}
