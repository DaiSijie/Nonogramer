/*
 *	Author:      Gilbert Maystre
 *	Date:        Sep 1, 2014
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import computation.Library;

@SuppressWarnings("serial")
public class DetailsAsker extends JPanel {
    
    private final JPanel mainPanel;
    private final JDialog associated;
    
    private final JTextField nameTextArea;
    private final JLabel nameOk;
  
    private final StarRating rating;
    
    private final JButton importAndPlay;
    
    private boolean wantedToPlay;

    public DetailsAsker(JDialog associated, String initName){
        this(associated);
        nameTextArea.setText(initName);
        
        if(Library.getInstanceOf().nameOk(nameTextArea.getText())){
            importAndPlay.setEnabled(true);
            nameOk.setForeground(Color.BLUE);
            nameOk.setText("✓");
        }
        else{
            importAndPlay.setEnabled(false);
            nameOk.setForeground(Color.RED);
            nameOk.setText("✗");
        }
    }
    
    public DetailsAsker(JDialog associated){
        mainPanel = new JPanel();
        this.associated = associated;
        
        nameTextArea = new JTextField();
        nameOk = new JLabel();
        
        rating = new StarRating(mainPanel);
        
        importAndPlay = new JButton();
        
        wantedToPlay = false;
        
        customizeComponents();
        placeComponents();
        addListenersToComponents();
    }
    
    private void customizeComponents(){
        nameTextArea.setToolTipText("A name not already in library and not containing \";\"");
        
        nameOk.setForeground(Color.RED);
        nameOk.setText("✗");
        
        importAndPlay.setText("Import and play!");
        importAndPlay.setEnabled(false);
    }

    private void placeComponents(){
        
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Name of the grid :"), BorderLayout.LINE_START);
        namePanel.add(nameTextArea, BorderLayout.CENTER);
        namePanel.add(nameOk, BorderLayout.LINE_END);

        JPanel ratingPanel = new JPanel();
        ratingPanel.setLayout(new BoxLayout(ratingPanel, BoxLayout.X_AXIS));
        ratingPanel.add(Box.createHorizontalGlue());
        ratingPanel.add(rating);
        ratingPanel.add(Box.createHorizontalGlue());
        
        JPanel importPanel = new JPanel();
        importPanel.setLayout(new BoxLayout(importPanel, BoxLayout.X_AXIS));
        importPanel.add(Box.createHorizontalGlue());
        importPanel.add(importAndPlay);
        importPanel.add(Box.createHorizontalGlue());
        
        JPanel submainPanel = new JPanel();
        submainPanel.setLayout(new BoxLayout(submainPanel, BoxLayout.Y_AXIS));
        submainPanel.add(Box.createVerticalGlue());
        submainPanel.add(Box.createVerticalStrut(10));
        submainPanel.add(namePanel);
        submainPanel.add(Box.createVerticalStrut(10));
        submainPanel.add(ratingPanel);
        submainPanel.add(Box.createVerticalStrut(10));
        submainPanel.add(importPanel);
        submainPanel.add(Box.createVerticalStrut(10));
        submainPanel.add(Box.createVerticalGlue());
        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(Box.createHorizontalStrut(10));
        mainPanel.add(submainPanel);
        mainPanel.add(Box.createHorizontalStrut(10));
        
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
        
        
        
        
    }
    
    private void addListenersToComponents(){
        
        nameTextArea.addCaretListener(new CaretListener(){

            @Override
            public void caretUpdate(CaretEvent e) {                
                if(Library.getInstanceOf().nameOk(nameTextArea.getText())){
                    importAndPlay.setEnabled(true);
                    nameOk.setForeground(Color.BLUE);
                    nameOk.setText("✓");
                }
                else{
                    importAndPlay.setEnabled(false);
                    nameOk.setForeground(Color.RED);
                    nameOk.setText("✗");
                }
                
            }
            
        });

        importAndPlay.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                wantedToPlay = true;
                associated.dispatchEvent(new WindowEvent(associated, WindowEvent.WINDOW_CLOSING));
            }
            
        });
        
    }
    
    public String getGridName(){
        return nameTextArea.getText();
    }

    public int getGridLevel(){
        return rating.getLevelWanted();
    }

    public boolean wantedToPlay(){
        return wantedToPlay;
    }
    
}

