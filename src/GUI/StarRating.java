/*
 *	Author:      Gilbert Maystre
 *	Date:        Sep 2, 2014
 */

package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class StarRating extends JComponent{
    
    private BufferedImage starOff;
    private BufferedImage starOn;

    private int toPaint;
    private int actual;

    private final JComponent enclosingMainPanel;

    public StarRating(JComponent enclosingMainPanel){
        
        try {
            starOff = ImageIO.read(getClass().getResourceAsStream("/StarOff.png"));
            starOn = ImageIO.read(getClass().getResourceAsStream("/StarOn.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.toPaint = 1;
        this.actual = 1;
        
        this.enclosingMainPanel = enclosingMainPanel;
        

        this.setMaximumSize(getPreferredSize());
        this.setMinimumSize(getPreferredSize());

        addListeners();
    }

    private void addListeners(){

        this.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                actual = toPaint;
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

        });

        this.addMouseMotionListener(new MouseMotionListener(){

            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                toPaint = e.getPoint().x/50+1;
                repaint();
            }

        });
        
        enclosingMainPanel.addMouseMotionListener(new MouseMotionListener(){

            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                toPaint = actual;
                repaint();
            }

        });

    }

    public int getLevelWanted(){
        return actual;
    }
    
    @Override
    public Dimension getPreferredSize(){
        int width = 2+2+5*50+2+2+2; //marge espace etoile etoile etoile etoile etoile espace marge
        int height = 2+2+50+2+2+2; //marge espace etoile espace marge
        
        return new Dimension(width, height);
    }

    @Override
    public void paintComponent(Graphics g0){
        Graphics2D g = (Graphics2D) g0;
        
        g.setColor(Color.WHITE);
        g.fill(getVisibleRect());

        for(int i = 0; i<5; i++){
            g.drawImage(i<toPaint? starOn : starOff, 4+i*50, 4, null);
        }

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.draw(new Rectangle2D.Double(1,1,2+2+5*50+2+2,2+2+50+2+2));

    }

}
