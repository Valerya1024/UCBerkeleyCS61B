public class NBody {
    public static void main(String[] args){
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        double R = readRadius(filename);
        Planet[] allPlanets = readPlanets(filename);

        StdDraw.enableDoubleBuffering();
        StdDraw.setScale(-R, R);

        double time = 0;
        while (time <= T) {
            double[] FnxList = new double[allPlanets.length];
            double[] FnyList = new double[allPlanets.length];
            int i = 0;
            for (Planet p : allPlanets){
                double Fnx = p.calcNetForceExertedByX(allPlanets);
                double Fny = p.calcNetForceExertedByY(allPlanets);
                FnxList[i] = Fnx;
                FnyList[i] = Fny;
                i++;
            }
            StdDraw.clear();
            StdDraw.picture(0, 0, "images/starfield.jpg", 2*R, 2*R);
            i = 0;
            for (Planet p : allPlanets){
                p.update(dt, FnxList[i], FnyList[i]);
                p.draw();
                i++;
            }
            StdDraw.show();
            StdDraw.pause(10);
            time = time + dt;
        }

    }

    public static double readRadius(String filename) {
        In in = new In(filename);
        in.readInt();
        double R = in.readDouble();

        return R;
    }

    public static Planet[] readPlanets(String filename){
        In in = new In(filename);
        int n = in.readInt();
        in.readDouble();
        Planet[] allPlanets = new Planet[n];

        for (int i = 0; i < n; i++){
            double xP = in.readDouble();
            double yP = in.readDouble();
            double xV = in.readDouble();
            double yV = in.readDouble();
            double m = in.readDouble();
            String img = in.readString();

            allPlanets[i] = new Planet(xP, yP, xV, yV, m, img);
        }
    return allPlanets;
    }
}