/*
 *	Author:      Gilbert Maystre
 *	Date:        Aug 22, 2014
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import computation.Library;
import computation.NonogramGrid;
import computation.Parsers;

public class NonogramerInterface1 {

    private final JPanel mainPanel;
    private final JLabel nonogramerLogo;

    private final JComboBox<String> libraryComboBox;
    private final JButton startFromLibraryComboBox;
    private final JButton deleteGrid;
    private final JButton setGrid;
    
    private final JTextField codingFlowInput;
    private final JButton startFromCodingFlowInput;
    private final JCheckBox addToLibraryFromCodingFlowInput;

    private final JButton openPic;
    private final JTextField picName;
    private final JButton startFromPic;
    private final JCheckBox addToLibraryFromPic;
    private final SpinnerNumberModel naturalsModelForRows;
    private final SpinnerNumberModel naturalsModelForColumns;
    private final JSpinner nbOfRows;
    private final JSpinner nbOfColumns;

    private final JFileChooser picChooser;
    private File choosedFile;
    
    private final StarRating rater;
    private final JButton startFromRandom;

    private final JButton help;
    private final JPanel helpPanel;
    private final JButton restoreLibrary;

    private final JFrame mainFrame;

    public static void main(String[] args) {
        new NonogramerInterface1();
    }

    public NonogramerInterface1(){
        this.mainPanel = new JPanel();
        this.nonogramerLogo = new JLabel();

        this.libraryComboBox = new JComboBox<>();
        this.startFromLibraryComboBox = new JButton();
        this.deleteGrid = new JButton();
        this.setGrid = new JButton();

        this.codingFlowInput = new JTextField();
        this.startFromCodingFlowInput = new JButton();
        this.addToLibraryFromCodingFlowInput = new JCheckBox();

        this.openPic = new JButton();
        this.picName = new JTextField();
        this.startFromPic = new JButton();
        this.addToLibraryFromPic = new JCheckBox();
        this.naturalsModelForRows = new SpinnerNumberModel(10, 1, 100, 1);
        this.naturalsModelForColumns = new SpinnerNumberModel(10, 1, 100, 1);
        this.nbOfRows = new JSpinner();
        this.nbOfColumns = new JSpinner();

        this.picChooser = new JFileChooser();
        
        this.rater = new StarRating(mainPanel);
        this.startFromRandom = new JButton();

        this.help = new JButton();
        this.helpPanel = constructHelpPanel();
        this.restoreLibrary = new JButton();

        this.mainFrame = new JFrame();

        customizeComponents();
        placeComponents();
        addListenersToComponents();
    }

    private JPanel constructHelpPanel(){
        //etape 1 : récuperer le texte
        StringBuilder textBuilder = new StringBuilder();
        
        InputStream inStream = getClass().getResourceAsStream("/infoText.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        JEditorPane displayer = new JEditorPane();
        displayer.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        displayer.setEditable(false);
        displayer.setText(textBuilder.toString());
        
        displayer.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if(Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        
        
        JScrollPane jsp = new JScrollPane(displayer);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        
        JPanel toReturn = new JPanel(new BorderLayout());
        toReturn.add(jsp, BorderLayout.CENTER);
        
        return toReturn;
    }
        
    private void customizeComponents(){
        BufferedImage icon = null;

        try {
            icon = ImageIO.read(getClass().getResourceAsStream("/nonogramer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        nonogramerLogo.setIcon(new ImageIcon(icon));

        updateLibraryComboBox();
        startFromLibraryComboBox.setText("Play!");
        deleteGrid.setText("Delete");
        setGrid.setText("Manage");
        
        startFromCodingFlowInput.setText("Play!");
        addToLibraryFromCodingFlowInput.setSelected(false);

        openPic.setText("Open...");
        picName.setText("no file selected");
        picName.setEditable(false);
        startFromPic.setText("Play!");
        startFromPic.setEnabled(false);
        addToLibraryFromPic.setSelected(false);
        nbOfRows.setModel(naturalsModelForRows);
        nbOfColumns.setModel(naturalsModelForColumns);
        (((JSpinner.DefaultEditor) nbOfRows.getEditor()).getTextField()).setEditable(false);
        (((JSpinner.DefaultEditor) nbOfColumns.getEditor()).getTextField()).setEditable(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Graphical formats", "jpg", "gif", "png", "bmp", "wbmp");
        picChooser.setAcceptAllFileFilterUsed(false); 
        picChooser.setFileFilter(filter);
        
        startFromRandom.setText("Play!");

        help.setText("?");
        restoreLibrary.setText("Restore library");
    }

    private void placeComponents(){
        //1er panneau
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(new JLabel(" Choose a way to open a nonogram :"), BorderLayout.LINE_START);

        //2eme panneau        
        JPanel libraryInputLowFlow = new JPanel(new FlowLayout());
        libraryInputLowFlow.add(new JLabel("Edit grid :"));
        libraryInputLowFlow.add(deleteGrid);
        libraryInputLowFlow.add(setGrid);
        
        JPanel libraryInputLow = new JPanel(new BorderLayout());
        libraryInputLow.add(libraryInputLowFlow, BorderLayout.LINE_START);
        
        JPanel libraryInput = new JPanel(new BorderLayout());
        libraryInput.add(libraryComboBox, BorderLayout.CENTER);
        libraryInput.add(startFromLibraryComboBox, BorderLayout.LINE_END);
        libraryInput.add(libraryInputLow, BorderLayout.PAGE_END);
        
        libraryInput.setBorder(BorderFactory.createTitledBorder("From library"));

        //3eme panneau
        JPanel flowInputLow = new JPanel(new FlowLayout());
        flowInputLow.add(new JLabel("Add to library"));
        flowInputLow.add(addToLibraryFromCodingFlowInput);
        JPanel flowInputLowLeft = new JPanel(new BorderLayout());
        flowInputLowLeft.add(flowInputLow, BorderLayout.LINE_START);

        JPanel flowInput = new JPanel(new BorderLayout());
        flowInput.add(codingFlowInput, BorderLayout.CENTER);
        flowInput.add(startFromCodingFlowInput, BorderLayout.LINE_END);
        flowInput.add(flowInputLowLeft, BorderLayout.PAGE_END);

        flowInput.setBorder(BorderFactory.createTitledBorder("From ImageCodingComparator"));

        //4eme panneau
        JPanel picPanelHigh = new JPanel(new BorderLayout());
        picPanelHigh.add(openPic, BorderLayout.LINE_START);
        picPanelHigh.add(picName, BorderLayout.CENTER);
        picPanelHigh.add(startFromPic, BorderLayout.LINE_END);

        JPanel picPanelMediumFlow = new JPanel(new FlowLayout());
        picPanelMediumFlow.add(new JLabel("Rows :"));
        picPanelMediumFlow.add(nbOfRows);
        picPanelMediumFlow.add(new JSeparator());
        picPanelMediumFlow.add(new JLabel("Columns :"));
        picPanelMediumFlow.add(nbOfColumns);

        JPanel picPanelMedium = new JPanel(new BorderLayout());
        picPanelMedium.add(picPanelMediumFlow, BorderLayout.LINE_START);

        JPanel picPanelLowFlow = new JPanel(new FlowLayout());
        picPanelLowFlow.add(new JLabel("Add to library"));
        picPanelLowFlow.add(addToLibraryFromPic);

        JPanel picPanelLow = new JPanel(new BorderLayout());
        picPanelLow.add(picPanelLowFlow, BorderLayout.LINE_START);

        JPanel picInput = new JPanel();
        picInput.setLayout(new BoxLayout(picInput, BoxLayout.Y_AXIS));
        picInput.add(picPanelHigh);
        picInput.add(picPanelMedium);
        picInput.add(picPanelLow);

        picInput.setBorder(BorderFactory.createTitledBorder("From a pixelised picture"));

        //5eme panneau
        JPanel randomInput = new JPanel(new FlowLayout());
        randomInput.add(rater);
        randomInput.add(startFromRandom);
        randomInput.setBorder(BorderFactory.createTitledBorder("Random data"));
            
        //6eme panneau
        JPanel settingsPanel = new JPanel(new FlowLayout());
        settingsPanel.add(help);
        settingsPanel.add(restoreLibrary);

        //Panneau des entrées
        JPanel inputs = new JPanel();
        inputs.setLayout(new BoxLayout(inputs, BoxLayout.PAGE_AXIS));
        inputs.add(messagePanel);
        inputs.add(libraryInput);
        inputs.add(flowInput);
        inputs.add(picInput);
        inputs.add(randomInput);
        inputs.add(settingsPanel);

        //Panneau du logo
        JPanel logo = new JPanel(new BorderLayout());
        logo.setLayout(new BoxLayout(logo, BoxLayout.X_AXIS ));
        logo.add(Box.createHorizontalGlue());
        logo.add(nonogramerLogo);
        logo.add(Box.createHorizontalGlue());

        //Panneau général
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(logo, BorderLayout.CENTER);
        mainPanel.add(inputs, BorderLayout.PAGE_END);

        //Consctruction de la fenêtre
        mainFrame.setContentPane(mainPanel);
        mainFrame.setMinimumSize(new Dimension(mainPanel.getPreferredSize().width + 20, mainPanel.getPreferredSize().height+20));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("@Nonogramer");
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void addListenersToComponents(){

        startFromLibraryComboBox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String raw = (String) libraryComboBox.getSelectedItem();
                String parsed = raw.substring(0, raw.indexOf("★")-1);
                new Nonogram(Library.getInstanceOf().getGridForName(parsed), mainFrame);
            }
        });
        
        deleteGrid.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                
                String raw = (String) libraryComboBox.getSelectedItem();
                String name = raw.substring(0, raw.indexOf("★")-1);
                
                int choice = JOptionPane.showConfirmDialog(mainFrame,"Do you really want to delete \""+name+"\" ?", "", JOptionPane.YES_NO_OPTION);
                
                if(choice == JOptionPane.YES_OPTION){
                    Library.getInstanceOf().deleteGrid(name);
                    updateLibraryComboBox();
                }
            }
        });
        
        setGrid.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String raw = (String) libraryComboBox.getSelectedItem();
                String oldName = raw.substring(0, raw.indexOf("★")-1);
                
                int oldLevel = Library.getInstanceOf().getGridForName(oldName).getLevel();
                String rawData = Library.getInstanceOf().getRawDataFor(oldName);
                
                Library.getInstanceOf().deleteGrid(oldName);
                
                final JDialog dialog = new JDialog(mainFrame, "Set name and level", Dialog.ModalityType.APPLICATION_MODAL);

                DetailsAsker asker = new DetailsAsker(dialog, oldName);

                dialog.setContentPane(asker);
                dialog.setLocationRelativeTo(mainFrame);
                dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                dialog.pack();
                dialog.setResizable(false);
                dialog.setVisible(true);
                
                if(!asker.wantedToPlay()){
                    Library.getInstanceOf().addGridToLibrary(oldName, oldLevel, rawData);
                }
                else{
                    Library.getInstanceOf().addGridToLibrary(asker.getGridName(), asker.getGridLevel(), rawData);
                    updateLibraryComboBox();
                }
            }
        });

        startFromCodingFlowInput.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    String rawFlow = Parsers.imageCodingComparatorToRawFlow(codingFlowInput.getText());
                    NonogramGrid parsed = Parsers.rawFlowToNonogramGrid(rawFlow, "Imported from Image Coding Comparator");

                    if(addToLibraryFromCodingFlowInput.isSelected()){

                        //we launch a small dialog that will ask for details
                        final JDialog dialog = new JDialog(mainFrame, "Import details", Dialog.ModalityType.APPLICATION_MODAL);

                        DetailsAsker asker = new DetailsAsker(dialog);

                        dialog.setContentPane(asker);
                        dialog.setLocationRelativeTo(mainFrame);
                        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        dialog.pack();
                        dialog.setResizable(false);
                        dialog.setVisible(true);

                        //si le joueur a fermé la fenêtre on ne fait rien
                        if(!asker.wantedToPlay())
                            return;

                        Library.getInstanceOf().addGridToLibrary(asker.getGridName(), asker.getGridLevel(), rawFlow);
                        parsed.setName(asker.getGridName());
                        parsed.setLevel(asker.getGridLevel());

                        updateLibraryComboBox();
                    }

                    new Nonogram(parsed, mainFrame);
                }
                catch(IllegalArgumentException exception){
                    JOptionPane.showMessageDialog(mainFrame, "Unable to parse grid ("+exception.getMessage()+")","Error",JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        startFromPic.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {

                BufferedImage nonogramerBuffered = null;
                try {
                    nonogramerBuffered = ImageIO.read(choosedFile);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(mainFrame, "Problem occured while accessing file", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String purifiedName = choosedFile.getName();
                int pos = purifiedName.lastIndexOf(".");
                if (pos > 0)
                    purifiedName = purifiedName.substring(0, pos);

                String rawFlow = Parsers.photoToRawFlow(nonogramerBuffered, (Integer) nbOfRows.getValue(), (Integer) nbOfColumns.getValue());
                NonogramGrid parsed = Parsers.rawFlowToNonogramGrid(rawFlow, purifiedName);

                if(addToLibraryFromPic.isSelected()){
                    //we launch a small dialog that will ask for details
                    final JDialog dialog = new JDialog(mainFrame, "Import details", Dialog.ModalityType.APPLICATION_MODAL);

                    DetailsAsker asker = new DetailsAsker(dialog, purifiedName);

                    dialog.setContentPane(asker);
                    dialog.setLocationRelativeTo(mainFrame);
                    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    dialog.pack();
                    dialog.setResizable(false);
                    dialog.setVisible(true);

                    //si le joueur a fermé la fenêtre on ne fait rien
                    if(!asker.wantedToPlay())
                        return;

                    Library.getInstanceOf().addGridToLibrary(asker.getGridName(), asker.getGridLevel(), rawFlow);

                    parsed.setName(asker.getGridName());
                    parsed.setLevel(asker.getGridLevel());

                    updateLibraryComboBox();
                }

                new Nonogram(parsed, mainFrame);
            }
        });

        openPic.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int returnVal = picChooser.showOpenDialog(mainFrame);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    startFromPic.setEnabled(true);
                    addToLibraryFromPic.setEnabled(true);
                    choosedFile = picChooser.getSelectedFile();
                    picName.setText(choosedFile.getName());
                }
            }
        });
        
        startFromRandom.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                
                int level = rater.getLevelWanted();
                NonogramGrid toPlay = Parsers.rawFlowToNonogramGrid(Parsers.randomRawFlow(level), "Random grid");
                toPlay.setLevel(level);
                
                new Nonogram(toPlay, mainFrame);
            }
        });

        help.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog infoDialog = new JDialog(mainFrame, "Infos");
                infoDialog.setModal(true);
                infoDialog.setContentPane(helpPanel);
                infoDialog.pack();
                infoDialog.setSize(500,400);
                infoDialog.setResizable(false);
                infoDialog.setLocationRelativeTo(null);
                infoDialog.setVisible(true);  
            }
   
        });
        
        
        restoreLibrary.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int choice = JOptionPane.showConfirmDialog(mainFrame,"Do you really want to restore the library?\nAll added grids will be erased.", "", JOptionPane.YES_NO_OPTION);                
                
                if(choice == JOptionPane.YES_OPTION){
                    Library.getInstanceOf().restore();
                    updateLibraryComboBox();
                }
                
            }
        });
        
        
    }

    private void updateLibraryComboBox(){
        libraryComboBox.removeAllItems();

        boolean somethingWasAdded = false;

        for(int i = 1; i <= 5; i++){

            StringBuilder builder = new StringBuilder();
            for(int j = 0; j<i; j++){
                builder.append("★");
            }
            String suffix = builder.toString();

            if(Library.getInstanceOf().getGridsNameForLevel(i)!= null){
                ArrayList<String> grids = new ArrayList<>(Library.getInstanceOf().getGridsNameForLevel(i));
                Collections.sort(grids);

                for(String name : grids){
                    libraryComboBox.addItem(name+" "+suffix);
                    somethingWasAdded = true;
                }
            }
        }

        if(!somethingWasAdded)
            libraryComboBox.addItem("No grids were found");

        libraryComboBox.setEnabled(somethingWasAdded);
        startFromLibraryComboBox.setEnabled(somethingWasAdded);
        deleteGrid.setEnabled(somethingWasAdded);
    }

}
