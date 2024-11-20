/*
 *	Author:      Gilbert Maystre
 *	Date:        Aug 12, 2014
 */

package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import computation.NonogramGrid;
import relations.Observer;
import relations.Subject;
import static GUI.Nonogram.FLAWLESS_TILE_SIZE;

@SuppressWarnings("serial")
public final class NonogramPlayer extends JComponent implements Subject{

    private final NonogramGrid grid;

    private final int maxInformativeForColumns;
    private final int maxInformativeForRows;
    private final int length;
    private final int height;

    private final Type[][] cellTypes;
    private final State[][] cellStates;

    private final ArrayList<Observer> observers;

    private int cursorX;
    private int cursorY;

    private Color informativeUnselected;
    private Color informativeSelected;
    private Color background;
    private Color cursor;

    private JPopupMenu popupMenu;

    public NonogramPlayer(NonogramGrid grid){
        loadTheme(Theme.DEEP_PEACE);

        this.grid = grid;

        this.maxInformativeForColumns = grid.getMaxInformativeForColumns();
        this.maxInformativeForRows = grid.getMaxInformativeForRows();
        this.length = grid.getNumberOfColumn()+maxInformativeForRows;
        this.height = grid.getNumberOfRow()+maxInformativeForColumns;

        this.cellTypes = new Type[height][length];
        this.cellStates = new State[height][length];

        for(int i = 0; i < height; i++){
            for(int j = 0; j < length; j++){
                boolean y = i < maxInformativeForColumns;
                boolean x = j < maxInformativeForRows; 

                if(x && y)//case vide
                    this.cellTypes[i][j] = Type.NOTHING;

                else if(x ^ y){//case possiblement d'info
                    if(!x){//on regarde des colonnes
                        int relative = j - maxInformativeForRows;
                        int size = NonogramGrid.getInformativeFor(grid.getNthColumn(relative)).size();
                        this.cellTypes[i][j] = (i >= maxInformativeForColumns - size)? Type.INFORMATIVE : Type.NOTHING;                        
                    }

                    else{//on regarde les lignes
                        int relative = i - maxInformativeForColumns;
                        int size = NonogramGrid.getInformativeFor(grid.getNthRow(relative)).size();
                        this.cellTypes[i][j] = (j >= maxInformativeForRows - size)? Type.INFORMATIVE : Type.NOTHING;
                    }
                }

                else //case de nonograme
                    this.cellTypes[i][j] = Type.PIXEL;

                //on met finalement l'état des céllules qui sont vides par défaut
                this.cellStates[i][j] = State.EMPTY;
            }
        }

        this.observers = new ArrayList<>();

        this.cursorX = maxInformativeForRows;
        this.cursorY = maxInformativeForColumns;

        constructPopupMenu();

        this.addListeners();
    }

    private void constructPopupMenu(){
        this.popupMenu = new JPopupMenu();

        JRadioButtonMenuItem basical = new JRadioButtonMenuItem("Deep peace");

        basical.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                loadTheme(Theme.DEEP_PEACE);
                repaint();
            }
        });

        JRadioButtonMenuItem red = new JRadioButtonMenuItem("Performance storm");

        red.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                loadTheme(Theme.PERFORMANCE_STORM);
                repaint();
            }
        });

        JRadioButtonMenuItem random = new JRadioButtonMenuItem("I'm feeling lucky");

        random.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                loadTheme(Theme.RANDOM);
                repaint();
            }
        });



        ButtonGroup b = new ButtonGroup();
        b.add(basical);
        b.add(red);
        b.add(random);

        basical.setSelected(true);

        popupMenu.add(basical);
        popupMenu.add(red);
        popupMenu.add(random);



    }

    private void addListeners(){
        this.setFocusable(true);
        this.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                int keyNumber = e.getKeyCode();

                boolean repaintNeed = false;

                if(!popupMenu.isShowing()){
                    if(keyNumber == KeyEvent.VK_RIGHT && isCursableCell(cursorY, cursorX+1)){
                        repaintNeed = true;
                        cursorX++;
                    }

                    if(keyNumber == KeyEvent.VK_LEFT && isCursableCell(cursorY, cursorX-1)){
                        repaintNeed = true;
                        cursorX--;

                    }

                    if(keyNumber == KeyEvent.VK_UP && isCursableCell(cursorY-1, cursorX)){
                        repaintNeed = true;
                        cursorY--;
                    }

                    if(keyNumber == KeyEvent.VK_DOWN && isCursableCell(cursorY+1, cursorX)){
                        repaintNeed = true;
                        cursorY++;
                    }

                    if(keyNumber == KeyEvent.VK_ENTER){
                        repaintNeed = true;
                        cellStates[cursorY][cursorX] = State.nextState(cellStates[cursorY][cursorX], cellTypes[cursorY][cursorX]);
                    }
                }

                if(repaintNeed){
                    repaint();
                    if(keyNumber == KeyEvent.VK_ENTER){
                        notifyObservers();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}

        });


        this.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.isPopupTrigger())
                    popupMenu.show(NonogramPlayer.this, e.getPoint().x, e.getPoint().y);
                
                requestFocus();

            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
                
                if(e.isPopupTrigger())
                    popupMenu.show(NonogramPlayer.this, e.getPoint().x, e.getPoint().y);
                else{

                    int sizeX = (int) Math.round(NonogramPlayer.this.getSize().width/(double)length);
                    int sizeY = (int) Math.round(NonogramPlayer.this.getSize().height/(double)height);

                    cursorX = e.getPoint().x / sizeX;
                    cursorY = e.getPoint().y / sizeY;

                    if(!isCursableCell(cursorY, cursorX)){
                        cursorX = maxInformativeForRows;
                        cursorY = maxInformativeForColumns;
                    }
                    else if(isCursableCell(cursorY, cursorX) && hasFocus()){
                        cellStates[cursorY][cursorX] = State.nextState(cellStates[cursorY][cursorX], cellTypes[cursorY][cursorX]);
                        notifyObservers();
                    }


                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub
                requestFocus();

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

        });


        this.addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
                repaint();

            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();

            }

        });
    }

    private boolean isCursableCell(int row, int column){
        boolean rowOk = 0 <= row && row < height;
        boolean columnOk = 0 <= column && column < length;

        return rowOk && columnOk && (cellTypes[row][column] == Type.INFORMATIVE || cellTypes[row][column] == Type.PIXEL);
    }

    public State[][] getCellStates(){
        return this.cellStates;
    }

    private void paintText(int row, int column, String text, Graphics2D g){

        double rectSideX = getSize().getWidth()/length;
        double rectSideY = getSize().getHeight()/height;

        Rectangle2D box = g.getFont().getStringBounds(text, g.getFontRenderContext());

        float x = (float) (column*rectSideX + (rectSideX - box.getWidth())/2d);
        float y = (float) (row*rectSideY + (rectSideY + box.getHeight())/2d);

        g.drawString(text, x, y);
    }

    private void loadTheme(Theme newTheme){
        if(newTheme  == Theme.DEEP_PEACE){
            informativeUnselected = new Color(170, 224, 172);
            informativeSelected = new Color(70, 152, 252);
            background = Color.LIGHT_GRAY;
            cursor = Color.BLUE;  
        }
        else if(newTheme == Theme.PERFORMANCE_STORM){
            informativeSelected = new Color(237, 59, 59);
            informativeUnselected = new Color(219, 186, 0);
            background = new Color(158, 43, 43);
            cursor = Color.red;
        }
        else if(newTheme == Theme.RANDOM){
            Random rand = new Random();
            informativeUnselected = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            informativeSelected = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            background = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            cursor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());

        } 
    }

    @Override
    public void paintComponent(Graphics g0){
        Graphics2D g = (Graphics2D) g0;

        g.setColor(background);
        g.fill(getVisibleRect());

        double rectSideX = getSize().getWidth()/length;
        double rectSideY = getSize().getHeight()/height;



        for(int i = 0; i < height; i++){
            for(int j = 0; j < length; j++){

                Rectangle2D.Double box = new Rectangle2D.Double(rectSideX*j, rectSideY*i, rectSideX, rectSideY);

                if(cellTypes[i][j] == Type.PIXEL){
                    g.setColor(Color.WHITE);
                    g.fill(box);
                }

                else if(cellTypes[i][j] == Type.INFORMATIVE){
                    g.setColor(((i == cursorY || j == cursorX) && cursorY >= maxInformativeForColumns && cursorX >= maxInformativeForRows && hasFocus())? informativeSelected : informativeUnselected);
                    g.fill(box);
                }

                //on dessine ou non la grille
                if(cellTypes[i][j] == Type.INFORMATIVE || cellTypes[i][j] == Type.PIXEL){
                    g.setColor(Color.black);
                    g.draw(box);
                }
            }
        }

        //on dessine maintenant toutes les informatives sur les colonnes 
        for(int i = 0; i < grid.getNumberOfColumn(); i++){

            ArrayList<Integer> informative = NonogramGrid.getInformativeFor(grid.getNthColumn(i));
            int startK = maxInformativeForColumns - informative.size();

            for(int k = 0; k < informative.size(); k++){
                paintText(startK + k, maxInformativeForRows + i, informative.get(k).toString(), g);
            }
        }

        //puis toutes les informatives sur les lignes
        for(int i = 0; i < grid.getNumberOfRow(); i++){

            ArrayList<Integer> informative = NonogramGrid.getInformativeFor(grid.getNthRow(i));
            int startK = maxInformativeForRows - informative.size();

            for(int k = 0; k < informative.size(); k++){
                paintText(maxInformativeForColumns + i, startK + k, informative.get(k).toString(), g);
            }
        }

        //puis tout les états différents
        for(int i = 0; i<height; i++){
            for(int j = 0; j<length; j++){

                Rectangle2D.Double bounds = new Rectangle2D.Double(rectSideX*j, rectSideY*i, rectSideX, rectSideY);

                if(cellStates[i][j] == State.WHITE_SURE){
                    g.draw(new Line2D.Double(bounds.x, bounds.y+bounds.height, bounds.x+bounds.width, bounds.y));
                    g.draw(new Line2D.Double(bounds.x, bounds.y, bounds.x+bounds.width, bounds.y+bounds.height));
                }

                else if(cellStates[i][j] == State.BLACK_SURE){
                    g.fill(bounds);
                }

                else if(cellStates[i][j] == State.CROSSED){
                    g.draw(new Line2D.Double(bounds.x, bounds.y+bounds.height, bounds.x+bounds.width, bounds.y));
                }
            }
        }

        //on dessine ensuite le curseur

        if(hasFocus()){
            g.setStroke(new BasicStroke(3));
            g.setColor(cursor);
            g.draw(new Rectangle2D.Double(rectSideX*cursorX, rectSideY*cursorY, rectSideX, rectSideY));
        }
    }

    public PlayState getState(){

        boolean pixelCorrect = true;

        for(int i = maxInformativeForColumns; i<cellStates.length; i++){
            for(int j = maxInformativeForRows; j<cellStates[i].length; j++){
                State current = cellStates[i][j];


                if(current == State.EMPTY){
                    return PlayState.UNFINISHED;
                }
                if((current == State.BLACK_SURE && grid.valueAt(i-maxInformativeForColumns, j-maxInformativeForRows))
                        || (current == State.WHITE_SURE && !grid.valueAt(i-maxInformativeForColumns, j-maxInformativeForRows)))
                    pixelCorrect = false;
            }
        }

        if(pixelCorrect){
            return PlayState.PIXEL_CORRECT;
        }


        boolean[][] newGrid = new boolean[cellStates.length-maxInformativeForColumns][cellStates[0].length-maxInformativeForRows];
        for(int i = maxInformativeForColumns; i<cellStates.length; i++){
            for(int j = maxInformativeForRows; j<cellStates[i].length; j++){
                newGrid[i-maxInformativeForColumns][j-maxInformativeForRows] = cellStates[i][j] == State.WHITE_SURE;
            }
        }

        NonogramGrid toCompare = new NonogramGrid(newGrid, "comp");

        //on compare toute les colonnes
        for(int i = 0; i<toCompare.getNumberOfColumn(); i++){
            if(!NonogramGrid.getInformativeFor(grid.getNthColumn(i)).equals(NonogramGrid.getInformativeFor(toCompare.getNthColumn(i))))
                return PlayState.WRONG;
        }

        //on compare toute les lignes
        for(int i = 0; i<toCompare.getNumberOfRow(); i++){
            if(!NonogramGrid.getInformativeFor(grid.getNthRow(i)).equals(NonogramGrid.getInformativeFor(toCompare.getNthRow(i))))
                return PlayState.WRONG;
        }

        return PlayState.INFORMATIVE_CORRECT;
    }



    @Override
    public Dimension getPreferredSize(){
        return new Dimension(length*FLAWLESS_TILE_SIZE, height*FLAWLESS_TILE_SIZE);
    }

    @Override
    public void addObserver(Observer obs) {
        observers.add(obs);
    }

    @Override
    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void notifyObservers() {
        for(Observer observer : observers){
            observer.update();
        }
    }

    private enum Type{
        NOTHING, INFORMATIVE, PIXEL;
    }

    public enum State{
        WHITE_SURE, BLACK_SURE, EMPTY, CROSSED;

        public static State nextState(State current, Type type){
            if(type == Type.NOTHING)
                throw new IllegalArgumentException("type -nothing- cannot have a state");

            if(type == Type.INFORMATIVE){
                if(current == WHITE_SURE || current == BLACK_SURE)
                    throw new IllegalArgumentException("type -informative- cannot have BLACK_SURE or WHITE_SURE state");

                if(current == EMPTY)
                    return CROSSED;
                else
                    return EMPTY;
            }
            else{
                if(current == CROSSED)
                    throw new IllegalArgumentException("type -pixel- cannot have CROSSED state");

                if(current == WHITE_SURE)
                    return BLACK_SURE;
                else if(current == BLACK_SURE)
                    return EMPTY;
                else
                    return WHITE_SURE;
            }       
        }
    }

    public enum PlayState{
        UNFINISHED, WRONG, PIXEL_CORRECT, INFORMATIVE_CORRECT;

        public static Color getAssociatedColor(PlayState state){
            switch(state){
            case UNFINISHED : return Color.BLUE;
            case WRONG : return Color.RED;
            case PIXEL_CORRECT : return Color.GREEN;
            case INFORMATIVE_CORRECT : return Color.BLUE;
            default : throw new IllegalArgumentException("meeh");
            }
        }

        public static String getAssociatedText(PlayState state){
            switch(state){
            case UNFINISHED : return "Unfinished grid";
            case WRONG : return "Incorrect grid";
            case PIXEL_CORRECT : return "Correct grid";
            case INFORMATIVE_CORRECT : return "Incorrect grid, but still matches informatives";
            default : throw new IllegalArgumentException("meeh");
            }
        }
    }

    private enum Theme{
        DEEP_PEACE, PERFORMANCE_STORM, RANDOM;
    }

}
