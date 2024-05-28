import acm.graphics.GCompound;
import acm.graphics.GRect;
import acm.graphics.*;

import java.awt.*;

public class Paddle extends GCompound{

    GImage paddle;
    public Paddle(String x, double width, double height){
        paddle = new GImage(x);
        paddle.setSize(width,height);
        paddle.setColor(Color.BLACK);
        paddle.setVisible(true);
        add(paddle);
    }
}
