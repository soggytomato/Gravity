import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Tail {

    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private float alpha;
    private float dAlpha;
    private Color color;
    private int duration = 10;
    private int width = 1;

    public Tail(int x1, int y1, int x2, int y2, Color c, float a) {

        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = c;
        alpha = 1f * a;
        dAlpha = alpha / duration;
    }

    // Functions

    public boolean update() {

        if (alpha - dAlpha >= 0f) {
            alpha -= dAlpha;
            return false;
        } else {
            return true;
        }
    }

    public void draw(Graphics2D g) {

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(color);
        Stroke s = g.getStroke();
        g.setStroke(new BasicStroke(width));
        g.drawLine(x1, y1, x2, y2);
        g.setStroke(s);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

    }
}