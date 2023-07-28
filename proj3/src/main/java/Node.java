import java.util.HashMap;
import java.util.Set;

public class Node {

   private long id;
   private double lat;
   private double lon;

   private HashMap<Long, Long> adjacent;

   public Node(long id, double lat, double lon){
      this.id = id;
      this.lat = lat;
      this.lon = lon;
      adjacent = new HashMap<>();
   }

   public void addEdge(long nodeId, long wayId) {
      adjacent.put(nodeId, wayId);
   }

   public boolean disconnected() {
      return adjacent.isEmpty();
   }

   public double getLat(){
      return lat;
   }

   public double getLon() {
      return lon;
   }

   public Set<Long> getAdjacent(){
      return adjacent.keySet();
   }

   public long getWay(long id) {
      return adjacent.get(id);
   }
}
