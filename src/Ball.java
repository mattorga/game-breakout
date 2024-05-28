import acm.graphics.*;

import java.awt.*;

public class Ball extends GCompound{

    public Ball(double width, double height) {
        GOval ball = new GOval(width, height);
        ball.setFilled(true);
        ball.setColor(Color.ORANGE);
        ball.setVisible(true);
        add(ball);
    }
}
