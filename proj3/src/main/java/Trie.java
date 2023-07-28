import java.util.*;

public class Trie {

   static final int ALPHABET_SIZE = 27;
   static final String NAME = "name";
   static final String ID = "id";
   static final String LAT = "lat";
   static final String LON = "lon";
   TrieNode root;


   static class TrieNode {
      List<Map<String, Object>> locs;
      TrieNode[] children;

      public TrieNode() {
         children = new TrieNode[ALPHABET_SIZE];
         locs = null;
         for (int i = 0; i < ALPHABET_SIZE; i++) {
            children[i] = null;
         }
      }

      public List<String> getRes(List<String> res) {
         if (locs != null) {
            List<String> newRes = new LinkedList<>();
            for (Map<String, Object> loc : locs) {
               if (!newRes.contains(loc.get(NAME))) {
                  newRes.add((String) loc.get(NAME));
               }
            }
            res.addAll(newRes);
         }
         for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (children[i] != null) {
               res = children[i].getRes(res);
            }
         }
         return res;
      }

   }

   public Trie() {
      root = new TrieNode();
   }

   public void insert(String str, long id, double lat, double lon){
      String cleanStr = GraphDB.cleanString(str);
      TrieNode node = root;
      for (int i = 0; i < cleanStr.length(); i++) {
         int charIdx;
         if (cleanStr.charAt(i) == ' ') {
            charIdx = 26;
         } else {
            charIdx = cleanStr.charAt(i) - 'a';
         }
         if (node.children[charIdx] == null) {
            node.children[charIdx] = new TrieNode();
         }
         node = node.children[charIdx];
      }
      if (node.locs == null) {
         node.locs = new ArrayList<>();
      }
      Map<String, Object> loc = new HashMap<>();
      loc.put(NAME, str);
      loc.put(ID, id);
      loc.put(LAT, lat);
      loc.put(LON, lon);
      node.locs.add(loc);
   }

   public List<String> search(String str){
      List<String> out = new LinkedList<>();
      TrieNode node = getNode(str);
      out = node.getRes(out);
      return out;
   }

   public List<Map<String, Object>> getLocs(String str) {
      TrieNode node = getNode(str);
      return node.locs;
   }

   private TrieNode getNode(String str) {
      String cleanStr = GraphDB.cleanString(str);
      System.out.println(cleanStr);
      TrieNode node = root;
      for (int i = 0; i < cleanStr.length(); i++) {
         int charIdx;
         if (cleanStr.charAt(i) == ' ') {
            charIdx = 26;
         } else {
            charIdx = cleanStr.charAt(i) - 'a';
         }
         if (node.children[charIdx] == null) {
            return null;
         }
         node = node.children[charIdx];
      }
      return node;
   }

}




