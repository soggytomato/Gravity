import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Explosion {

    private double x;
    private double y;
    private double r;
    private double dr;
    private double ar;
    private int baseDuration = 10;
    private int duration;
    private float alpha;
    private float dAlpha;
    private Color color;

    public Explosion(double x, double y, Color c, double e) {

        this.x = x;
        this.y = y;
        this.color = c;
        duration = baseDuration + (int) (e / 20);
        alpha = 0.5f;
        dAlpha = alpha / duration;
        r = 0;
        dr = 4;
        ar = dr / duration;
    }

    // Functions

    public boolean update() {

        if (alpha - dAlpha >= 0f) {
            alpha -= dAlpha;
            r += dr;
            dr -= ar;
            return false;
        } else {
            return true;
        }
    }

    public void draw(Graphics2D g) {

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(color);
        g.fillOval((int) (x - r), (int) (y - r), (int) (2 * r), (int) (2 * r));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}