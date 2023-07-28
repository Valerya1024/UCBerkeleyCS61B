import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {

    static class Vertex implements Comparable {

        double dist;
        double stDist;
        double desDist;
        long id;
        Long prevId;

        public Vertex(double stDist, double desDist, long id, Long prevId) {
            this.stDist = stDist;
            this.desDist = desDist;
            this.dist = stDist + desDist;
            this.id = id;
            this.prevId = prevId;
        }

        public void updateStDist(double stDist, long prevId) {
            if (stDist < this.stDist) {
                this.stDist = stDist;
                this.dist = stDist + desDist;
                this.prevId = prevId;
            }
        }

        @Override
        public int compareTo(Object o) {
            Vertex v = (Vertex) o;
            if (this.dist > v.dist) {
                return 1;
            } else if (this.dist == v.dist) {
                return 0;
            } else {
                return -1;
            }
        }

        @Override
        public String toString(){
            return "Dist: "+dist+", Id: "+id;
        }
    }

    /**
     * A* algorithm
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */

    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        Long stId = g.closest(stlon, stlat);
        Long desId = g.closest(destlon, destlat);
        List<Long> path = new ArrayList<>();
        TreeSet<Vertex> q = new TreeSet<>();
        Map<Long, Vertex> closed = new HashMap<>();
        Map<Long, Vertex> open = new HashMap<>();
        Vertex st = new Vertex(0, g.distance(stId, desId), stId, null);
        q.add(st);
        Vertex des = null;
        while (!q.isEmpty()){
            Vertex v = q.pollFirst();
            //System.out.println("Vertex Popped: "+v);
            closed.put(v.id, v);
            if (v.id == desId){
                des = v;
                break;
            }
            Iterable<Long> neighbors = g.adjacent(v.id);
            for (Long neighborId : neighbors) {
                if (!closed.containsKey(neighborId)) { // if not in closed set
                    if (!open.containsKey(neighborId)) { //push
                        Vertex n = new Vertex(v.stDist+g.distance(v.id, neighborId), g.distance(neighborId, desId), neighborId, v.id);
                        //System.out.println("Add Vertex: "+n);
                        q.add(n);
                        open.put(neighborId, n);
                    } else { //decreaseKey
                        q.remove(open.get(neighborId));
                        open.get(neighborId).updateStDist(v.stDist+g.distance(v.id, neighborId), v.id);
                        q.add(open.get(neighborId));
                    }
                }
            }
        }

        while (des.prevId != null) { //traceback
            path.add(des.id);
            des = closed.get(des.prevId);
        }
        path.add(des.id);
        Collections.reverse(path);
        return path;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        List<NavigationDirection> directions = new ArrayList<>();

        List<Way> ways = new ArrayList<>();
        long wayId = g.getWayId(route.get(0), route.get(1));
        Way way = g.getWay(wayId);
        ways.add(way);
        System.out.println(way);
        double distance = g.distance(route.get(0), route.get(1));
        int direction = NavigationDirection.START;
        for (int i = 1; i<route.size()-1; i++) {
            long newWayId = g.getWayId(route.get(i), route.get(i+1));
            Way newWay = g.getWay(newWayId);
            String newWayName = newWay.getName();
            if (!newWayName.equals(way.getName())) {
                NavigationDirection d = new NavigationDirection(direction, way.getName(), distance);
                directions.add(d);
                way = newWay;
                ways.add(newWay);
                distance = g.distance(route.get(i), route.get(i+1));
                double diff = g.bearing(route.get(i), route.get(i+1))-g.bearing(route.get(i-1), route.get(i));
                direction = NavigationDirection.getDirection(diff);
                System.out.println(diff + " " + NavigationDirection.getDirection(diff));
            } else {
                distance += g.distance(route.get(i), route.get(i+1));
            }
            //System.out.println(g.bearing(route.get(i), route.get(i+1)));
        }
        directions.add(new NavigationDirection(direction, way.getName(), distance));

        return directions;
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Integer constants representing boundaries of directions*/
        public static final int SLIGHTLY_LEFT_BD = -15;
        public static final int LEFT_BD = -30;
        public static final int SHARP_LEFT_BD = -100;
        public static final int SLIGHTLY_RIGHT_BD = 15;
        public static final int RIGHT_BD = 30;
        public static final int SHARP_RIGHT_BD = 100;
        public static final int BACK = 180;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public NavigationDirection(int direction, String wayName, double distance) {
            this.direction = direction;
            this.way = wayName;
            this.distance = distance;
        }

        public static int getDirection(double diff) {
            if (diff > BACK) {
                diff -= 360;
            } else if (diff < -BACK) {
                diff += 360;
            }
            int direction = 0;
            if (diff >= SLIGHTLY_LEFT_BD && diff <= SLIGHTLY_RIGHT_BD ) {
                direction = STRAIGHT;
            } else if (diff > SLIGHTLY_RIGHT_BD && diff <= RIGHT_BD) {
                direction = SLIGHT_RIGHT;
            } else if (diff > RIGHT_BD && diff <= SHARP_RIGHT_BD) {
                direction = RIGHT;
            } else if (diff > SHARP_RIGHT_BD && diff <= BACK) {
                direction = SHARP_RIGHT;
            } else if (diff < SLIGHTLY_LEFT_BD && diff >= LEFT_BD) {
                direction = SLIGHT_LEFT;
            } else if (diff < LEFT_BD && diff >= SHARP_LEFT_BD) {
                direction = LEFT;
            } else if (diff < SHARP_LEFT_BD && diff >= -BACK) {
                direction = SHARP_LEFT;
            }
            return direction;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }
                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }

    }
}
