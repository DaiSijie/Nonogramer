/*
 *	Author:      Gilbert Maystre
 *	Date:        Aug 21, 2014
 */

package computation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

public final class Parsers {

    private final static Random rand = new Random();

    private Parsers(){};

    public static String imageCodingComparatorToRawFlow(String codingFlow) throws IllegalArgumentException{

        if(codingFlow.length() < 8)
            throw new IllegalArgumentException("Bad size");

        if(!codingFlow.startsWith("10101010"))
            throw new IllegalArgumentException("Unrecognised encoding");

        String properFlow = codingFlow.substring(8);

        int size = (int)(Math.sqrt(properFlow.length()));

        if(size*size != properFlow.length())
            throw new IllegalArgumentException("Bad size");

        for(int i = 0; i<properFlow.length(); i++){
            char token = properFlow.charAt(i);

            if(token != '0' && token != '1')
                throw new IllegalArgumentException("Bad token");
        }

        StringBuilder builder = new StringBuilder();
        builder.append(size);
        builder.append("x");
        builder.append(size);
        builder.append("x");
        builder.append(properFlow);

        return builder.toString();
    }

    public static String photoToRawFlow(BufferedImage photo, int numberOfRows, int numberOfColumns) throws IllegalArgumentException{
        if(numberOfRows > photo.getHeight() || numberOfColumns > photo.getWidth())
            throw new IllegalArgumentException("Bad size");

        double pixelLength = photo.getWidth()/(double)numberOfColumns;
        double pixelHeight = photo.getHeight()/(double)numberOfRows;

        StringBuilder builder = new StringBuilder();
        builder.append(numberOfRows);
        builder.append("x");
        builder.append(numberOfColumns);
        builder.append("x");

        for(int i = 0; i<numberOfRows; i++){
            for(int j = 0; j<numberOfColumns; j++){
                builder.append(parseInfoFromColor(new Color(photo.getRGB((int) (pixelLength/2+j*pixelLength), (int) ( pixelHeight/2+i*pixelHeight)))) ? '1' : '0');
            }
        }

        return builder.toString();
    }

    public static NonogramGrid rawFlowToNonogramGrid(String codingFlow, String name) throws IllegalArgumentException{
        String[] regex = codingFlow.split("x");

        int numberOfRows = new Integer(regex[0]);
        int numberOfColumns = new Integer(regex[1]);
        String properFlow = regex[2];

        if(numberOfColumns*numberOfRows != properFlow.length())
            throw new IllegalArgumentException("Bad chain size");

        boolean[][] grid = new boolean[numberOfRows][numberOfColumns];

        int count = 0;

        for(int i = 0; i<numberOfRows; i++){
            for(int j = 0; j<numberOfColumns; j++){

                char current = properFlow.charAt(count);
                count++;

                if(current != '0' && current != '1')
                    throw new IllegalArgumentException("Bad token");

                grid[i][j] = (current == '1')? true : false;

            }
        }

        return new NonogramGrid(grid, name);
    }

    public static String randomRawFlow(int level){
        if(!(1 <= level && level <= 5))
            throw new IllegalArgumentException("Level out of range");

        int nbOfRows = 0;
        int nbOfColumns = 0;

        switch(level){
        case 1 : nbOfRows = generateRandom(3,5); nbOfColumns = generateRandom(3,5);
        break;
        case 2 : nbOfRows = generateRandom(7,10); nbOfColumns = generateRandom(7,10);
        break;
        case 3 : nbOfRows = generateRandom(12,15); nbOfColumns = generateRandom(12,15);
        break;
        case 4 : nbOfRows = generateRandom(17,20); nbOfColumns = generateRandom(17,20);
        break;
        case 5 : nbOfRows = generateRandom(23,30); nbOfColumns = generateRandom(23,30);
        break;
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append(nbOfRows);
        builder.append("x");
        builder.append(nbOfColumns);
        builder.append("x");
        
        for(int i = 0; i<nbOfRows; i++){
            for(int j = 0; j<nbOfColumns; j++){
                builder.append(rand.nextBoolean()? '1' : '0');
            }
        }
           
        return builder.toString();
    }

    private static boolean parseInfoFromColor(Color c){
        Color black = Color.BLACK;
        Color white = Color.WHITE;

        double dstToBlack = Math.sqrt(Math.pow(black.getBlue()-c.getBlue(),2)+Math.pow(black.getGreen()-c.getGreen(),2)+Math.pow(black.getRed()-c.getRed(),2));
        double dstToWhite = Math.sqrt(Math.pow(white.getBlue()-c.getBlue(),2)+Math.pow(white.getGreen()-c.getGreen(),2)+Math.pow(white.getRed()-c.getRed(),2));

        return dstToBlack > dstToWhite;
    }

    public static int generateRandom(int minInclusive, int maxInclusive){
        if(minInclusive < 0 || maxInclusive < minInclusive)
            throw new IllegalArgumentException("out of bounds");

        int delta = maxInclusive - minInclusive;

        return rand.nextInt(delta+1)+minInclusive;
    }

}
