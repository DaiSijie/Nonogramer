/*
 *	Author:      Gilbert Maystre
 *	Date:        Sep 9, 2014
 */

package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CustomPanel extends JPanel{
    
    private static final int FLUENT_SIZE = 25;
    
//    private static final int EMPTY = 0;
    private static final int WHITE = 1;
    private static final int BLACK = 2;
    
    private final Random rand;
    private final HashMap<Point, Integer> types;
    
    public CustomPanel(){
        rand = new Random();
        types = new HashMap<>();
    }
    
    @Override
    public void paintComponent(Graphics g0){
        Graphics2D g = (Graphics2D) g0;
        
        Point2D.Double center = new Point2D.Double(getVisibleRect().width/2d, getVisibleRect().height/2d);
        Point2D.Double banaf = new Point2D.Double(center.x - FLUENT_SIZE/2d, center.y - FLUENT_SIZE/2d);
        
        g.setColor(Color.WHITE);
        g.fill(getVisibleRect());
        
        g.setColor(Color.BLACK);
        
        for(int i = 0; i*FLUENT_SIZE < center.x + FLUENT_SIZE; i++){
            for(int j = 0; j*FLUENT_SIZE < center.y + FLUENT_SIZE; j++){
                
                //if want mirror.
//                int status = determine(i, j);
                
                if(i == 0 && j == 0){
                    drawSpecialized(determine(0, 0), new Rectangle2D.Double(banaf.x, banaf.y, FLUENT_SIZE, FLUENT_SIZE), g);
                }
                else if(i == 0 && j != 0){
                    drawSpecialized(determine(0, j), new Rectangle2D.Double(banaf.x, banaf.y + j * FLUENT_SIZE, FLUENT_SIZE, FLUENT_SIZE), g);
                    drawSpecialized(determine(0, -j), new Rectangle2D.Double(banaf.x, banaf.y - j * FLUENT_SIZE, FLUENT_SIZE, FLUENT_SIZE), g);
                }
                else if(i != 0 && j == 0){
                    drawSpecialized(determine(i, 0), new Rectangle2D.Double(banaf.x + i * FLUENT_SIZE, banaf.y, FLUENT_SIZE, FLUENT_SIZE), g);
                    drawSpecialized(determine(-i, 0), new Rectangle2D.Double(banaf.x - i * FLUENT_SIZE, banaf.y, FLUENT_SIZE, FLUENT_SIZE), g);
                }
                else{
                    drawSpecialized(determine(i, j), new Rectangle2D.Double(banaf.x + i * FLUENT_SIZE, banaf.y + j * FLUENT_SIZE, FLUENT_SIZE, FLUENT_SIZE), g);
                    drawSpecialized(determine(i, -j), new Rectangle2D.Double(banaf.x + i * FLUENT_SIZE, banaf.y - j * FLUENT_SIZE, FLUENT_SIZE, FLUENT_SIZE), g);                   
                    drawSpecialized(determine(-i, j), new Rectangle2D.Double(banaf.x - i * FLUENT_SIZE, banaf.y + j * FLUENT_SIZE, FLUENT_SIZE, FLUENT_SIZE), g);
                    drawSpecialized(determine(-i, -j), new Rectangle2D.Double(banaf.x - i * FLUENT_SIZE, banaf.y - j * FLUENT_SIZE, FLUENT_SIZE, FLUENT_SIZE), g);                    
                }
            }
        }   
    }
    
    private void drawSpecialized(int status, Rectangle2D.Double target, Graphics2D g){
        if(status == BLACK)
            g.fill(target);
        else{
            g.draw(target);
            if(status == WHITE){
                g.draw(new Line2D.Double(target.getMinX(), target.getMinY(), target.getMaxX(), target.getMaxY()));
                g.draw(new Line2D.Double(target.getMinX(), target.getMaxY(), target.getMaxX(), target.getMinY()));
            }
        }
    }
    
    private int determine(int x, int y){
        Point toGo = new Point(x, y);
        if(!types.containsKey(toGo))
            types.put(toGo, rand.nextInt(3));
        
        return types.get(toGo);
    }
    
    
}