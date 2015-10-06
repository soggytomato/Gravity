import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

public class AppPanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    // Fields
    public static int WIDTH = 1600;
    public static int HEIGHT = 900;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    private int FPS = 60;
    private double averageFPS;
    public double spawnChance = 0.2;

    private int initialNodes = 40;
    private int maxNodes = 1000;
    private int particlesPerClick = 30;
    private double generatedSpeed = 10;
    private double initSpeed = 2;

    public static int totalNodes = 0;
    public static double totalKineticEnergy = 0;

    public static ArrayList<Node> nodes;
    public static ArrayList<Tail> tails;
    public static ArrayList<Explosion> explosions;
    private List<Node> toAdd;
    private boolean clearAll;
    private boolean updatingNodes;

    public AppPanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void run() {

        running = true;
        clearAll = false;
        updatingNodes = false;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHints(rh);

        nodes = new ArrayList<Node>();
        toAdd = new ArrayList<Node>();
        tails = new ArrayList<Tail>();
        explosions = new ArrayList<Explosion>();

        long startTime;
        long URDTimeMs;
        long waitTime;
        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = 30;

        long targetTime = 1000 / FPS;

        startNodes();

        while(running) {

            startTime = System.nanoTime();

            gameUpdate();
            gameRender();
            gameDraw();

            URDTimeMs = (System.nanoTime() - startTime) / 1000000;

            waitTime = targetTime - URDTimeMs;

            try {
                Thread.sleep(waitTime);
            }
            catch(Exception e) {

            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
            }

        }
    }

    private void gameUpdate() {

        updateNodes();
        updateTails();
        updateExplosions();
        generateNodes();
        if (clearAll) {
            nodes.clear();
        }
    }

    private void gameRender() { // draw to an off-screen image first
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + (int) averageFPS, 10, 20);
        g.drawString("Particles: " + totalNodes, 10, 40);
        g.drawString("Total kinetic energy: " + (int) totalKineticEnergy, 10, 60);
        g.drawString("Click anywhere to create " + particlesPerClick + " particles.", 10, 80);

        drawExplosions();
        drawNodes();
        drawTails();

    }

    private void gameDraw() { // draw to game screen
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    public void keyPressed(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if (keyCode == KeyEvent.VK_SPACE) {
            clearAll = true;
        }
    }
    public void keyReleased(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if (keyCode == KeyEvent.VK_SPACE) {
            clearAll = false;
        }
    }
    public void keyTyped(KeyEvent key) {}

    public void mouseMoved(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {

    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
        if (!updatingNodes) {
            for (int i = 0; i < particlesPerClick; i++) {
                Node node = new Node(e.getX(), e.getY(), 0.5f, generatedSpeed);
                toAdd.add(node);
            }
        }
    }

    public void updateNodes() {
        updatingNodes = true;
        for (Node n: toAdd) {
            nodes.add(n);
        }
        updatingNodes = false;
        toAdd.clear();
        for (int i = 0; i < nodes.size(); i++) {
            boolean remove = nodes.get(i).update();
            if(remove) {
                nodes.remove(i);
                i--;
            }
        }
        totalNodes = nodes.size();
        double tempKE = 0;
        for (Node n: nodes) {
            tempKE += n.kineticEnergy;
        }
        totalKineticEnergy = tempKE;
    }

    public void updateTails() {
        for (int i = 0; i < tails.size(); i++) {
            boolean remove = tails.get(i).update();
            if(remove) {
                tails.remove(i);
                i--;
            }
        }
    }

    public void updateExplosions() {
        for (int i = 0; i < explosions.size(); i++) {
            boolean remove = explosions.get(i).update();
            if(remove) {
                explosions.remove(i);
                i--;
            }
        }
    }

    public void generateNodes() {
        if (Math.random() < spawnChance && totalNodes <= maxNodes) {
            int x = (int) (Math.random() * WIDTH);
            int y = (int) (Math.random() * HEIGHT);
            Node node = new Node(x, y, 0f, initSpeed);
            toAdd.add(node);
        }

    }

    public void drawNodes() {
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).draw(g);
        }
    }

    public void drawTails() {
        for (int i = 0; i < tails.size(); i++) {
            tails.get(i).draw(g);
        }
    }

    public void drawExplosions() {
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).draw(g);
        }
    }

    public void startNodes() {
        for (int i = 0; i < initialNodes; i++) {
            int x = (int) (Math.random() * WIDTH);
            int y = (int) (Math.random() * HEIGHT);
            Node node = new Node(x, y, 0f, initSpeed);
            toAdd.add(node);
        }
    }

    public static void addTail(Tail t) {
        tails.add(t);
    }

    public static void addExplosion(Explosion e) {
        explosions.add(e);
    }

    public static void removeNode(Node n) {
        nodes.remove(n);
    }

}
