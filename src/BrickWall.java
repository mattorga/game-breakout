import acm.graphics.*;

public class BrickWall extends GCompound {

    GImage brick;
    public BrickWall(String x){
        brick = new GImage(x);
        brick.setSize(60, 20);
        brick.setVisible(true);
        add(brick,0,0);
        //markAsComplete();
    }
}
