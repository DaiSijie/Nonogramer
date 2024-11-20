/*
 *	Author:      Gilbert Maystre
 *	Date:        Feb 5, 2015
 */

package GUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ChallengeButton extends JButton {

    private BufferedImage on = null;
    private BufferedImage off = null;
    private boolean isOn;

    public ChallengeButton(){
        
        try {
            on = ImageIO.read(getClass().getResourceAsStream("/challenge_on.png"));
            off = ImageIO.read(getClass().getResourceAsStream("/challenge_off.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.isOn = false;
        this.setRolloverEnabled(true);

        
        this.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                isOn = !isOn;
                repaint();
             }
        });
        

    }

    @Override
    public Dimension getPreferredSize(){
        return new Dimension(200,50);
    }
    
    @Override
    public Dimension getMinimumSize(){
        return getPreferredSize();
    }

    @Override
    public void paintComponent(Graphics g){
        g.drawImage(isOn? on : off, 0, 0, null);
    }
}
