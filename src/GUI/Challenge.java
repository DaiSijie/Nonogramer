/*
 *	Author:      Gilbert Maystre
 *	Date:        Feb 5, 2015
 */

package GUI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import computation.NonogramGrid;
import computation.Parsers;

public class Challenge {

    private final JFrame frame;
    private final ChallengeButton startChallenge;
    private final static int LEVEL = 1;
    private final JPanel mainPanel;
    private final static int NB_OF_GAMES = 10;
    private int remainingGrids = NB_OF_GAMES;
    
    private final StarRating level;
    private final StarRating nbOfGames;
    
    
    public static void main(String[] args) {
        new Challenge();
    }
    
    public Challenge(){

        this.frame = new JFrame();
        this.mainPanel = new JPanel();
        this.startChallenge = new ChallengeButton();
        this.level = new StarRating(mainPanel);
        this.nbOfGames = new StarRating(mainPanel);
        customizeComponents();
        placeComponents();
        addListenerToComponents();
    }
    
    public void customizeComponents(){
        
        
        this.mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

    }
    
    public void placeComponents(){
        this.mainPanel.add(Box.createVerticalStrut(10));
        this.mainPanel.add(startChallenge);
        this.mainPanel.add(Box.createVerticalStrut(10));
        this.mainPanel.add(level);
        this.mainPanel.add(Box.createVerticalStrut(10));
        this.mainPanel.add(nbOfGames);
        
        
        
        this.frame.setContentPane(mainPanel);
        
        this.frame.setSize(400, 300);
        this.frame.setLocationRelativeTo(null);
        this.frame.setTitle("Nongrammer - The Challenge");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
    }
    
    public void addListenerToComponents(){
        this.startChallenge.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                int level = 2;
                NonogramGrid toPlay = Parsers.rawFlowToNonogramGrid(Parsers.randomRawFlow(level), "Random grid");
                toPlay.setLevel(level);
                
                new Nonogram(toPlay, frame);
                System.out.println("hi???");
                
            }
            
        });
    }
    
    

}
