public class Planet {
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;

    public Planet(double xP, double yP, double xV, double yV, double m, String img){
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }

    public Planet(Planet p){
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    public double calcDistance(Planet p){
        double dx = p.xxPos - this.xxPos;
        double dy = p.yyPos - this.yyPos;
        double distance = dx*dx + dy*dy;
        distance = Math.sqrt(distance);

        return distance;
    }

    public double calcForceExertedBy(Planet p){
        double r = calcDistance(p);
        double G = 6.67e-11;
        double F = G*this.mass*p.mass/(r*r);

        return F;
    }

    public double calcForceExertedByX(Planet p){
        double r = calcDistance(p);
        double dx = p.xxPos - this.xxPos;
        double F = calcForceExertedBy(p);
        double Fx = F*dx/r;

        return Fx;
    }

    public double calcForceExertedByY(Planet p){
        double r = calcDistance(p);
        double dy = p.yyPos - this.yyPos;
        double F = calcForceExertedBy(p);
        double Fy = F*dy/r;

        return Fy;
    }

    public double calcNetForceExertedByX(Planet[] allPlanets){
        double Fnx = 0;
        for (Planet p : allPlanets){
            if (p.equals(this)) {
                continue;
            } else {
                Fnx = Fnx + calcForceExertedByX(p);
            }
        }

        return Fnx;
    }

    public double calcNetForceExertedByY(Planet[] allPlanets){
        double Fny = 0;
        for (Planet p : allPlanets){
            if (p.equals(this)) {
                continue;
            } else {
                Fny = Fny + calcForceExertedByY(p);
            }
        }

        return Fny;
    }

    public void update(double dt, double Fx, double Fy){
        double ax = Fx/this.mass;
        double ay = Fy/this.mass;
        this.xxVel = this.xxVel + ax*dt;
        this.yyVel = this.yyVel + ay*dt;
        this.xxPos = this.xxPos + this.xxVel*dt;
        this.yyPos = this.yyPos + this.yyVel*dt;
    }

    public void draw(){
        StdDraw.picture(this.xxPos,this.yyPos,"images/"+this.imgFileName);
    }
}
