import acm.graphics.*;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;
import java.awt.*;
import java.awt.event.MouseEvent;

public class BreakoutRevisited extends GraphicsProgram {

    // Breakout CONSTANTS
    private static final int CANVAS_WIDTH = 640;
    private static final int CANVAS_HEIGHT = 720;
    private static final int NBRICKS_PER_ROW = 10;
    private static final int NBRICKS_PER_COL = 10;
    private static final double BRICK_GAP = 4;
    private static final double BRICK_WIDTH = Math.floor(
            (CANVAS_WIDTH - (NBRICKS_PER_ROW + 1.0) * BRICK_GAP) / NBRICKS_PER_ROW);
    private static final double BRICK_HEIGHT = 20;
    private static final double BRICK_Y_OFFSET = 60;   // from top
    private static final double PADDLE_WIDTH = 80;
    private static final double PADDLE_HEIGHT = 15;
    private static final double PADDLE_Y_OFFSET = 50;    // from bottom
    private static final double BALL_RADIUS = 12;
    private static final double VELOCITY_Y = 3.0;
    private static final double VELOCITY_X_MIN = 1.0;
    private static final double VELOCITY_X_MAX = 3.0;
    private static final int BALL_DELAY = 1000 / 60;

    // Breakout class variables: main actors in the app
    private Paddle paddle;
    private Ball ball;
    private BrickWall brick;
    // Ball velocity
    private double dx;
    private double dy;
    // Ball direction randomizer
    private RandomGenerator rgen = RandomGenerator.getInstance();

    //Score-Endgame Variables
    int numScore = 0;
    int numTurns = 3;
    private GLabel score;
    private GLabel turns;
    boolean reset = false;
    private GLabel endLose;
    private GLabel endWin;
    private GLabel signature;

    public void run() {
        setTitle("Breakout Game");
        setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        setBackground(Color.BLACK);

        // Set the size of the image to match the canvas size
        setSize(CANVAS_WIDTH, CANVAS_HEIGHT);

        gameState();

        createTiles(); //Loads Tiles

        createPaddle(); //Loads Paddle

        createBall(BALL_RADIUS); //Loads Ball

        addMouseListeners();

        ballPhysics();

        if (numTurns == 0){
            remove(paddle);
            remove(ball);
            add(endLose, getXCenter(endLose), getYCenter(endLose));
        }
        if (numScore == 200){
            add(endWin, getXCenter(endWin), getYCenter(endWin));
        }
    }

    public void gameState(){
        Font font = new Font(Font.SERIF, Font.BOLD, 24);
        Font sig = new Font(Font.SERIF, Font.BOLD, 12);

        score = new GLabel("Score: " + numScore);
        score.setFont(font);
        score.setColor(Color.WHITE);
        add(score, getWidth()-score.getWidth()-50,40);

        turns = new GLabel("Turns: " + numTurns);
        turns.setFont(font);
        turns.setColor(Color.WHITE);
        add(turns, 10, 40);

        endLose = new GLabel("GAME OVER");
        endLose.setFont(font);
        endLose.setColor(Color.WHITE);

        endWin = new GLabel("You win!");
        endWin.setFont(font);
        endWin.setColor(Color.WHITE);

        signature = new GLabel("Matthew Orga");
        signature.setFont(sig);
        signature.setColor(Color.WHITE);
        add(signature,10, CANVAS_HEIGHT-45);
    }

    /**
     * Loads all the tiles
     * Broken tiles are under the clean tiles
     */
    public void createTiles(){
        double columnalGap = BRICK_GAP+BRICK_HEIGHT;
        double rowGap = BRICK_GAP+BRICK_WIDTH;

        for (int i = 0; i < NBRICKS_PER_ROW; i++){
            for (int j = 0; j<NBRICKS_PER_COL;j++){
                brick = new BrickWall("assets/02_brick.png");
                add(brick, BRICK_GAP+rowGap*i, BRICK_Y_OFFSET+columnalGap*j);

                brick = new BrickWall("assets/01_brick.png");
                add(brick, BRICK_GAP+rowGap*i, BRICK_Y_OFFSET+columnalGap*j);
            }
        }
    }

    public void createPaddle(){
        paddle = new Paddle("assets/paddle.png", PADDLE_WIDTH, PADDLE_HEIGHT);
        add(paddle, getXCenter(paddle), getHeight()-PADDLE_Y_OFFSET);
    }

    public void createBall(double r){
        ball = new Ball(r*2, r*2);
        add(ball, getXCenter(ball), getYCenter(ball));
    }

    public void mouseMoved(MouseEvent mouse) {
        double mouseX = mouse.getX();

        if (mouseX > getHeight()-paddle.getWidth()){
            //Do nothing
        } else
        {
            paddle.setLocation(mouseX, getHeight() - PADDLE_Y_OFFSET);
        }
    }

    public void ballPhysics() {
        dx = rgen.nextDouble(VELOCITY_X_MIN, VELOCITY_X_MAX);
        dy = VELOCITY_Y;

        while (numTurns > 0) {
            if (ball.getX() < getWidth()-(getWidth()-1) || ball.getX()+2*BALL_RADIUS>getWidth()){
                dx = -dx;            }
            if (ball.getY() < BRICK_Y_OFFSET || ball.getY()+2*BALL_RADIUS>getHeight()){
                dy = -dy;
            }

            ball.move(dx, dy);
            colliderCheck(); //Collides with another GObject

            /**
             * Resets the state of the ball
             */
            if (reset){
                ball.setLocation(getXCenter(ball), getYCenter(ball));
                pause(1000);
                reset = false;
            }

            pause(BALL_DELAY);

            // Add the image to the canvas
        }
    }

    /**
     * Returns a negative dy value when collision happens
     * Updates scores and turns
     */
    public double colliderCheck(){
        GObject collider_TLeft = getElementAt(ball.getX(), ball.getY());
        GObject collider_TRight = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
        GObject collider_BLeft = getElementAt(ball.getX(), ball.getY()+2*BALL_RADIUS);
        GObject collider_BRight = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY()+2*BALL_RADIUS);

        if (collider_BLeft == paddle || collider_BRight == paddle){
            return dy=-dy;
        }

        if (ball.getY()+2*BALL_RADIUS>getHeight()){
            numTurns -= 1;
            turns.setLabel("Turns: "+numTurns);
            reset = true;
        }
        if (collider_TLeft != null) {
            remove(collider_TLeft);
            numScore++;
            score.setLabel("Score: " + numScore);
            return dy = -dy;
        } else if (collider_TRight != null) {
            remove(collider_TRight);
            numScore++;
            score.setLabel("Score: " + numScore);
            return dy = -dy;
        } else if (collider_BRight != null) {
            remove(collider_BRight);
            numScore++;
            score.setLabel("Score: " + numScore);
            return dy = -dy;
        } else if (collider_BLeft != null) {
            remove(collider_BLeft);
            numScore++;
            score.setLabel("Score: " + numScore);
            return dy = -dy;
        }
        return 0;
    }

    public double getXCenter(GObject g){
        return ((getWidth() - g.getWidth())/2);
    }
    public double getYCenter(GObject g){
        return ((getHeight() - g.getHeight())/2);
    }

    /* Solves NoClassDefFoundError */
    public static void main(String[] args) {
        new BreakoutRevisited().start(args);
    }
}
