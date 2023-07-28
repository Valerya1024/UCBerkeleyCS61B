import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    private HashMap<Long, Node> nodes;
    private HashMap<Long, Way> ways;
    private ArrayList<ArrayList<ArrayList<Long>>> posMap;
    private Trie locations;
    public static final int POS_MAP_LEVEL = 7;

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        nodes = new HashMap<>();
        ways = new HashMap<>();
        posMap = new ArrayList<>();
        locations = new Trie();
        int pos_map_div = (int) Math.pow(2, POS_MAP_LEVEL);
        for (int i = 0; i < pos_map_div; i++) {
            ArrayList<ArrayList<Long>> temp = new ArrayList<>();
            for (int j = 0; j < pos_map_div; j++) {
                ArrayList<Long> temp2 = new ArrayList<>();
                temp.add(temp2);
            }
            posMap.add(temp);
        }
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    public void addNode(long id, double lat, double lon) {
        nodes.put(id, new Node(id, lat, lon));
    }

    public void addEdge(long id1, long id2, long wayId) {
        nodes.get(id1).addEdge(id2, wayId);
        nodes.get(id2).addEdge(id1, wayId);
    }

    public void addWay(long wayId, int maxSpeed, String name){
        ways.put(wayId, new Way(wayId, maxSpeed, name));
    }

    public void addLocation(long id, String name, double lat, double lon){
        locations.insert(name, id, lat, lon);
    }

    public long getWayId(long id1, long id2) {
        long wayId = nodes.get(id1).getWay(id2);
        return wayId;
    }

    public Way getWay(long wayId) {
        return ways.get(wayId);
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph; add other nodes to posMap
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        ArrayList<Long> toRemove = new ArrayList<>();
        for (Map.Entry<Long, Node> e : nodes.entrySet()) {
            Node n = e.getValue();
            if (n.disconnected()) {
                toRemove.add(e.getKey());
            } else {
                int latIdx = Rasterer.getIdx(n.getLat(), true, POS_MAP_LEVEL);
                int lonIdx = Rasterer.getIdx(n.getLon(), false, POS_MAP_LEVEL);
                posMap.get(latIdx).get(lonIdx).add(e.getKey());
            }
        }
        for (Long id : toRemove) {
            nodes.remove(id);
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        return nodes.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        Node n = nodes.get(v);
        return n.getAdjacent();
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    public double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        int latIdx = Rasterer.getIdx(lat, true, POS_MAP_LEVEL);
        int lonIdx = Rasterer.getIdx(lon, false, POS_MAP_LEVEL);
        //System.out.println(latIdx+" "+lonIdx);
        ArrayList<Long> closeNodes = posMap.get(latIdx).get(lonIdx);
        int level = 0;
        int extra_level = 0;
        while (closeNodes.size() == 0 || extra_level == 0){
            level++;
            if (closeNodes.size() != 0){
                extra_level++;
            }
            for (int j = lonIdx - level; j < lonIdx + level + 1; j++) {
                if ( j >= 0 && j <= 127) {
                    if (latIdx - level >= 0 && latIdx - level <= 127) {
                        closeNodes.addAll(posMap.get(latIdx - level).get(j));
                    }
                    if (latIdx + level >= 0 && latIdx + level <= 127) {
                        closeNodes.addAll(posMap.get(latIdx + level).get(j));
                    }
                }
            }
            for (int i = latIdx - level + 1; i < latIdx + level + 1; i++) {
                if ( i >= 0 && i <= 127) {
                    if ( lonIdx - level >= 0 && lonIdx - level <= 127) {
                        closeNodes.addAll(posMap.get(i).get(lonIdx - level));
                    }
                    if ( lonIdx + level >= 0 && lonIdx + level <= 127) {
                        closeNodes.addAll(posMap.get(i).get(lonIdx + level));
                    }
                }

            }
        }
        double minDist = distance(MapServer.ROOT_LRLON, MapServer.ROOT_LRLAT, MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT);
        Long closestId = 0L;
        Long exp = 53076845L;
        for (Long id: closeNodes) {
            Node n = nodes.get(id);
            double dist = distance(lon, lat, n.getLon(), n.getLat());
            if (dist <= minDist) {
                minDist = dist;
                closestId = id;
            }
            if (exp == id) {
                System.out.println(minDist+" "+ dist);
            }
        }
        return closestId;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        return nodes.get(v).getLon();
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return nodes.get(v).getLat();
    }

    /**
     * Gets location names by prefix
     */
    public List<String> getLocationsByPrefix(String prefix) {
        List<String> res = locations.search(prefix);
        return res;
    }

    public List<Map<String, Object>> getLocations(String locationName) {
        return locations.getLocs(locationName);
    }
}


