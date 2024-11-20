/*
 *	Author:      Gilbert Maystre
 *	Date:        Aug 19, 2014
 */

package computation;

import java.util.ArrayList;

public final class NonogramGrid {

    private final int numberOfRow;
    private final int numberOfColumn;

    private final int maxInformativeForRows;
    private final int maxInformativeForColumns;

    private final boolean[][] grid;

    private String name;
    private int level;
    private boolean levelWasGiven;

    public NonogramGrid(boolean[][] grid, String name){

        if(grid.length == 0 || grid[0].length == 0)
            throw new IllegalArgumentException("empty grid");

        this.numberOfRow = grid.length;
        this.numberOfColumn = grid[0].length;

        this.grid = new boolean[numberOfRow][numberOfColumn];

        for(int i = 0; i<grid.length; i++){
            int currentColumnLength = grid[i].length;

            if(currentColumnLength != numberOfColumn)
                throw new IllegalArgumentException("malformed grid");

            for(int j = 0; j<grid[i].length; j++)
                this.grid[i][j] = grid[i][j];
        }

        this.maxInformativeForRows = computeMaxInformativeForRows();
        this.maxInformativeForColumns = computeMaxInformativeForColumns();

        this.name = name;

        this.levelWasGiven = false;
    }

    public int getNumberOfRow(){
        return this.numberOfRow;
    }

    public int getNumberOfColumn(){
        return this.numberOfColumn;
    }

    public boolean[] getNthRow(int n){ 
        boolean[] toReturn = new boolean[numberOfColumn];
        for(int i = 0; i<numberOfColumn; i++)
            toReturn[i] = grid[n][i];
        return toReturn;
    }

    public boolean[] getNthColumn(int n){
        boolean[] toReturn = new boolean[numberOfRow];
        for(int i = 0; i<numberOfRow; i++)
            toReturn[i] = grid[i][n];
        return toReturn;
    }

    public String getTitle(){
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        if(levelWasGiven){
            builder.append(" ");
            for(int i = 0; i<level; i++){
                builder.append("★");
            }
        }
        return builder.toString();
    }

    private int computeMaxInformativeForRows(){
        int max = 0;

        for(int i = 0; i<numberOfRow; i++){
            int informative = getInformativeFor(getNthRow(i)).size();
            if(informative > max)
                max = informative;
        }

        return max;
    }

    private int computeMaxInformativeForColumns(){
        int max = 0;

        for(int i = 0; i<numberOfColumn; i++){
            int informative = getInformativeFor(getNthColumn(i)).size();
            if(informative > max)
                max = informative;
        }

        return max;
    }

    public boolean valueAt(int row, int column){
        return this.grid[row][column];
    }

    public int getMaxInformativeForRows(){
        return this.maxInformativeForRows;
    }

    public int getMaxInformativeForColumns(){
        return this.maxInformativeForColumns;
    }

    public static ArrayList<Integer> getInformativeFor(boolean[] suite){
        int counter = 0;

        ArrayList<Integer> toReturn = new ArrayList<>();

        for(int i = 0; i<suite.length; i++){
            if(!suite[i])
                counter++;

            else if(counter != 0){
                toReturn.add(counter);
                counter = 0;
            }

            if(i == suite.length-1 && counter != 0)
                toReturn.add(counter);

        }

        return toReturn;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setLevel(int level){
        if(!(0 < level && level <= 5))
            throw new IllegalArgumentException("level out of range");
        
        this.levelWasGiven = true;
        this.level = level;
    }

    public int getLevel(){
        return level;
    }
}
