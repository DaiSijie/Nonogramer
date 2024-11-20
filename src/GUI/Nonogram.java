package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import computation.NonogramGrid;

import GUI.NonogramPlayer.PlayState;
import relations.Observer;

/*
 *	Author:      Gilbert Maystre
 *	Date:        Aug 12, 2014
 */

public final class Nonogram implements Observer{
    
    public static final int FLAWLESS_TILE_SIZE = 25;
    
    private final NonogramPlayer nonogramPlayer;
    private final Preview preview;
    
    private final JCheckBox statusHidder;
    private final JLabel status;
    
    public Nonogram(NonogramGrid game, Frame owner){

        this.nonogramPlayer = new NonogramPlayer(game);
        this.preview = new Preview(game, nonogramPlayer);
        
        this.statusHidder = new JCheckBox();
        this.status = new JLabel();
        
        customizeComponents();
        buildFrame(game, owner);
    }
    
    private void customizeComponents(){
        nonogramPlayer.addObserver(this);
        
        statusHidder.setSelected(true);
        statusHidder.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setVisible(!status.isVisible());
            }
        });
        
        status.setText("Unfinished grid");
        status.setForeground(PlayState.getAssociatedColor(PlayState.UNFINISHED));
    }
    
    private void buildFrame(NonogramGrid game, final Frame owner){
        JPanel nonogram = new JPanel(new BorderLayout());
        nonogram.add(nonogramPlayer, BorderLayout.CENTER);
        
        JPanel preview = new JPanel(new BorderLayout());
        preview.add(this.preview, BorderLayout.CENTER);
        
        JTabbedPane top = new JTabbedPane();
        top.add(nonogram, "Nonogram");
        top.add(preview, "Preview");
        
        JPanel statusLeft = new JPanel(new FlowLayout());
        statusLeft.add(new JLabel("Show status :"));
        statusLeft.add(statusHidder);
        
        JPanel statusRight = new JPanel(new FlowLayout());
        statusRight.add(status);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLeft, BorderLayout.LINE_START);
        statusPanel.add(statusRight, BorderLayout.LINE_END);
        
        JPanel main = new JPanel(new BorderLayout());
        main.add(top, BorderLayout.CENTER);
        main.add(statusPanel, BorderLayout.PAGE_END);
        
        JFrame frame = new JFrame();
        frame.setTitle(game.getTitle());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(main);
        frame.pack();
        frame.setLocationRelativeTo(owner);
        frame.setVisible(true);
    }
    
    @Override
    public void update(){
        PlayState current = nonogramPlayer.getState();
        status.setForeground(PlayState.getAssociatedColor(current));
        status.setText(PlayState.getAssociatedText(current));
    }
    
}
