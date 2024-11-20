/*
 *	Author:      Gilbert Maystre
 *	Date:        Aug 12, 2014
 */

package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import computation.NonogramGrid;

import GUI.NonogramPlayer.State;
import relations.Observer;
import static GUI.Nonogram.FLAWLESS_TILE_SIZE;

@SuppressWarnings("serial")
public final class Preview extends JComponent implements Observer{

    private final NonogramGrid grid;
    private final NonogramPlayer player;

    public Preview(NonogramGrid nonogram, NonogramPlayer player){
        this.grid = nonogram;
        this.player = player;

        this.player.addObserver(this);
    }

    @Override
    public void paintComponent(Graphics g0){
        Graphics2D g = (Graphics2D) g0;

        g.setColor(Color.LIGHT_GRAY);
        g.fill(getVisibleRect());

        //on regarde si l'ont peut mettre en flawless
        double tileSize = FLAWLESS_TILE_SIZE;
        double heapY = (this.getBounds().height - grid.getNumberOfRow()*tileSize)/2d;
        double heapX = (this.getBounds().width - grid.getNumberOfColumn()*tileSize)/2d;

        boolean flawlessMode = heapY > 0 && heapX > 0;

        if(!flawlessMode){
            tileSize = Math.min(getBounds().height/(double) grid.getNumberOfRow(), getBounds().width/(double) grid.getNumberOfColumn());
            heapY = (this.getBounds().height - grid.getNumberOfRow()*tileSize)/2d;
            heapX = (this.getBounds().width - grid.getNumberOfColumn()*tileSize)/2d;
        }

        int maxColumns = grid.getMaxInformativeForColumns();
        int maxRows = grid.getMaxInformativeForRows();

        for(int i = 0; i<grid.getNumberOfRow(); i++){
            for(int j = 0; j<grid.getNumberOfColumn(); j++){
                State cellState = player.getCellStates()[i+maxColumns][j+maxRows];
                boolean paintNeed = false;

                if(cellState == State.BLACK_SURE){
                    g.setColor(Color.BLACK);
                    paintNeed = true;
                }
                else if(cellState == State.WHITE_SURE){
                    g.setColor(Color.WHITE);
                    paintNeed = true;
                }

                if(paintNeed)
                    g.fill(new Rectangle2D.Double(heapX+j*tileSize, heapY+i*tileSize, tileSize, tileSize));
                
            }
        }
    }

    @Override
    public void update() {
        repaint();
    }

}
