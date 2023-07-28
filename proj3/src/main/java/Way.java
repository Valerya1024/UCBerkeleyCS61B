public class Way {
   private long id;
   private int maxSpeed;
   private String name;

   public Way(long id, int maxSpeed, String name) {
      this.id = id;
      this.maxSpeed = maxSpeed;
      this.name = name;
   }

   public long getId() {
      return id;
   }

   public double getMaxSpeed() {
      return maxSpeed;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return String.format("Way id: %d, name: %s %d", id, name, maxSpeed);
   }

   @Override
   public boolean equals(Object o){
      Way w = (Way) o;
      return w.name == name;
   }

}