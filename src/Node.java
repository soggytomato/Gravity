import java.awt.*;
import java.util.Random;

public class Node {

    private double speed;
    private double currentSpeed;
    private double x;
    private double y;
    private double dx;
    private double dy;
    private double angle;
    private int threshold = 350;
    private float alpha;
    private float dAlpha = 0.005f;
    private double maxAttraction = 0.03;
    private double annihilationEnergy = 1000;
    public double kineticEnergy;

    private int r = 1;


    private Color color;

    public Node(int x, int y, float alpha, double speed) {

        this.speed = speed;
        currentSpeed = speed;
        kineticEnergy = Math.pow(currentSpeed, 2);
        Random rand = new Random();
        angle = rand.nextDouble() * 2 * Math.PI;
        dx = speed * Math.cos(angle);
        dy = speed * Math.sin(angle);
        this.alpha = alpha;

        this.x = x;
        this.y = y;

        int red = 100 + (int) (Math.random() * 155);
        int green = 100 + (int) (Math.random() * 155);
        int blue = 100 + (int) (Math.random() * 155);
        color = new Color(red, green, blue);

    }

    public boolean update() {

        double xNew = x + dx;
        double yNew = y + dy;

        Tail tail = new Tail((int) x, (int) y, (int) xNew, (int) yNew, color, alpha);
        AppPanel.addTail(tail);

        x = xNew;
        y = yNew;

        interactAll();
        updateAlpha();

        currentSpeed = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        kineticEnergy = Math.pow(currentSpeed, 2);

        if (x < -r - threshold || x > AppPanel.WIDTH + r + threshold ||
                y < -r - threshold || y > AppPanel.HEIGHT + r + threshold) {
            return true;
        } else {
            return false;
        }
    }

    public void draw(Graphics2D g) {

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(color);
        g.fillOval((int) x - r, (int) y - r, 2 * r, 2 * r);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    public void interactAll() {
        for (int i = 0; i < AppPanel.nodes.size(); i++) {
            Node n = AppPanel.nodes.get(i);
            if (n != this) {
                double distance = Math.sqrt(Math.pow(this.x - n.x, 2) + Math.pow(this.y - n.y, 2));
                if (distance <= threshold) {
                    gravitate(n, distance);
                    double totalKineticEnergy = kineticEnergy + n.kineticEnergy;
                    if (distance <= r && totalKineticEnergy > annihilationEnergy) {
                        AppPanel.removeNode(n);
                        AppPanel.removeNode(this);

                        int rTemp = (n.getColor().getRed() + color.getRed()) / 2;
                        int gTemp = (n.getColor().getBlue() + color.getBlue()) / 2;
                        int bTemp = (n.getColor().getGreen() + color.getGreen()) / 2;
                        Color temp = new Color(rTemp, gTemp, bTemp);

                        Explosion explosion = new Explosion((this.x + n.x) / 2, (this.y + n.y) / 2, temp, totalKineticEnergy);
                        AppPanel.addExplosion(explosion);
                        i--;
                    }
                }
            }
        }
    }

    public void gravitate(Node n, double distance) {
        double attractionFactor = 1 - (distance / threshold);
        if (attractionFactor < 0) {
            attractionFactor = 0;
        } else if (attractionFactor > 1) {
            attractionFactor = 1;
        }
        double ax;
        double ay;
        double tempAngle = Math.atan((n.y - this.y) / (n.x - this.x));
        if (n.x > this.x) {
            ax = maxAttraction * Math.cos(tempAngle) * attractionFactor;
            ay = maxAttraction * Math.sin(tempAngle) * attractionFactor;
        } else {
            ax = -maxAttraction * Math.cos(tempAngle) * attractionFactor;
            ay = -maxAttraction * Math.sin(tempAngle) * attractionFactor;
        }


        dx += ax;
        dy += ay;
    }

    public void updateAlpha() {
        if (alpha + dAlpha <= 1f) {
            alpha += dAlpha;
        } else {
            alpha = 1f;
        }
    }

    public Color getColor() {
        return color;
    }

}