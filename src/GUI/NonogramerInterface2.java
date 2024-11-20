/*
 *	Author:      Gilbert Maystre
 *	Date:        Aug 22, 2014
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SizeRequirements;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

import computation.Library;
import computation.NonogramGrid;
import computation.Parsers;

public class NonogramerInterface2 {
    
    private static final Color BACKGROUND_COLOR = new Color(0, 169, 211, 250);
    
    //general 
    private final JFrame mainFrame;
    private final JLabel nonogramerLogo;
    private final JPanel floatingPanel;
    private final JPanel topPanel;
    private final JPanel[] downPanels;

    //General components
    private final JComboBox<String> inputChooser;
    private final JButton help;
    private final JPanel helpPanel;
    
    //Library input & settings aka downPanels@0
    private final JComboBox<String> libraryComboBox;
    private final JButton startFromLibraryComboBox;
    private final JButton deleteGrid;
    private final JButton setGrid;
    private final JButton restoreLibrary;
    
    //Coding flow input aka downPanels@1
    private final JTextPane codingFlowInput;
    private final JButton startFromCodingFlowInput;
    private final JCheckBox addToLibraryFromCodingFlowInput;

    //Pic input flow aka downPanels@2
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
    
    //Random input aka downPanels@3
    private final StarRating rater;
    private final JButton startFromRandom;

    public static void main(String[] args) {
        new NonogramerInterface2();
    }

    public NonogramerInterface2(){
        
        this.mainFrame = new JFrame();
        this.nonogramerLogo = new JLabel();
        this.floatingPanel = new JPanel();
        this.topPanel = new JPanel(){
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                
                Rectangle bounds = getVisibleRect();
                g.setColor(new Color(0, 40, 163));
                g.fillRect(bounds.x, bounds.y+bounds.height-4, bounds.width, 4);
            }
          };
        this.downPanels = new JPanel[4];
        
        this.inputChooser = new JComboBox<>();
        this.help = new JButton();
        this.helpPanel = constructHelpPanel();

        this.libraryComboBox = new JComboBox<>();
        this.startFromLibraryComboBox = new JButton();
        this.deleteGrid = new JButton();
        this.setGrid = new JButton();
        this.restoreLibrary = new JButton();

        this.codingFlowInput = new JTextPane();
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
        
        this.rater = new StarRating(floatingPanel);
        this.startFromRandom = new JButton();

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
        
    @SuppressWarnings("serial")
    private void customizeComponents(){
        BufferedImage icon = null;

        try {
            icon = ImageIO.read(getClass().getResourceAsStream("/nonogramer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        nonogramerLogo.setIcon(new ImageIcon(icon));

        inputChooser.addItem("Library");
        inputChooser.addItem("ICC");
        inputChooser.addItem("Pixelised picture");
        inputChooser.addItem("Random data");
        
        
        updateLibraryComboBox();
        startFromLibraryComboBox.setText("Play!");
        deleteGrid.setText("Delete");
        setGrid.setText("Manage");
        
        this.codingFlowInput.setEditorKit(new HTMLEditorKit(){

            ///////
            // see : http://java-sl.com/tip_html_letter_wrap.html
            ///////
            @Override 
            public ViewFactory getViewFactory(){ 

                return new HTMLFactory(){ 
                    public View create(Element e){ 
                        View v = super.create(e); 
                        if(v instanceof InlineView){ 
                            return new InlineView(e){ 
                                public int getBreakWeight(int axis, float pos, float len) { 
                                    return GoodBreakWeight; 
                                } 
                                public View breakView(int axis, int p0, float pos, float len) { 
                                    if(axis == View.X_AXIS) { 
                                        checkPainter(); 
                                        int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len); 
                                        if(p0 == getStartOffset() && p1 == getEndOffset()) { 
                                            return this; 
                                        } 
                                        return createFragment(p0, p1); 
                                    } 
                                    return this; 
                                } 
                            }; 
                        } 
                        else if (v instanceof ParagraphView) { 
                            return new ParagraphView(e) { 
                                protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) { 
                                    if (r == null) { 
                                        r = new SizeRequirements(); 
                                    } 
                                    float pref = layoutPool.getPreferredSpan(axis); 
                                    float min = layoutPool.getMinimumSpan(axis); 
                                    // Don't include insets, Box.getXXXSpan will include them. 
                                    r.minimum = (int)min; 
                                    r.preferred = Math.max(r.minimum, (int) pref); 
                                    r.maximum = Integer.MAX_VALUE; 
                                    r.alignment = 0.5f; 
                                    return r; 
                                } 

                            }; 
                        } 
                        return v; 
                    } 
                }; 
            } 
        });
        
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
        JPanel intermediateTopPanel = new JPanel();
        
        intermediateTopPanel.setLayout(new BoxLayout(intermediateTopPanel, BoxLayout.LINE_AXIS));
        intermediateTopPanel.add(Box.createHorizontalStrut(10));
        intermediateTopPanel.add(new JLabel("Menu : "));
        intermediateTopPanel.add(inputChooser);
        intermediateTopPanel.add(Box.createHorizontalStrut(10));
        intermediateTopPanel.add(Box.createHorizontalGlue());
        intermediateTopPanel.add(help);
        intermediateTopPanel.add(Box.createHorizontalStrut(10));
        intermediateTopPanel.setOpaque(false);
        
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(intermediateTopPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        topPanel.setBackground(BACKGROUND_COLOR);
        
        downPanels[0] = this.constuctLibraryPanel();
        downPanels[1] = this.constructCodingComparatorPanel();
        downPanels[2] = this.constructPicturePanel();
        downPanels[3] = this.constructRandomPanel();

        //Panneau flottant
        int prefWidth = 0;
        int prefHeight = 0;
        
        for(int i = 0; i<downPanels.length; i++){
            Dimension preferred = downPanels[i].getPreferredSize();
            
            int panelWidth = preferred.width;
            int panelHeight = preferred.height;
            
            if(panelWidth > prefWidth)
                prefWidth = panelWidth;
            if(panelHeight > prefHeight)
                prefHeight = panelHeight;
        }
        
        Dimension right = new Dimension(Math.max(prefWidth, topPanel.getPreferredSize().width), prefHeight + topPanel.getPreferredSize().height + 10 + 10 + 10);
        floatingPanel.setPreferredSize(right);
        floatingPanel.setMaximumSize(right);
        floatingPanel.setOpaque(false);
        
        floatingPanel.setLayout(new BorderLayout());
        floatingPanel.add(topPanel, BorderLayout.PAGE_START);
        floatingPanel.add(downPanels[0], BorderLayout.CENTER);
        floatingPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 40, 163), 4));
        
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.PAGE_AXIS));
        page.add(Box.createVerticalGlue());
        page.add(floatingPanel);
        page.add(Box.createVerticalGlue());
        page.setOpaque(false);
        
        final JPanel line = new JPanel();
        line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));
        line.add(Box.createHorizontalGlue());
        line.add(page);
        line.add(Box.createHorizontalGlue());
        line.setOpaque(false);

        final CustomPanel customPanel = new CustomPanel();
        final JLayeredPane layeredPane = new JLayeredPane();
        
        layeredPane.setPreferredSize(new Dimension(right.width+80, right.height+80));
        layeredPane.add(customPanel, new Integer(0));
        layeredPane.add(line, new Integer(1));

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                final Rectangle newBounds = layeredPane.getBounds();
                customPanel.setBounds(newBounds);
                line.setBounds(newBounds);

                line.revalidate();
                customPanel.revalidate();
            }
        });

        //Consctruction de la fenêtre
        mainFrame.setContentPane(layeredPane);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("@Nonogramer");
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void addListenersToComponents(){
        inputChooser.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                floatingPanel.removeAll();
                floatingPanel.add(topPanel, BorderLayout.PAGE_START);
                floatingPanel.add(downPanels[inputChooser.getSelectedIndex()], BorderLayout.CENTER);

                
                floatingPanel.revalidate();
                floatingPanel.repaint();

                
            }
            
        });
        
        
        
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

    private JPanel constuctLibraryPanel(){
        JPanel page = new JPanel();
        page.setOpaque(false);
        page.setLayout(new BoxLayout(page, BoxLayout.PAGE_AXIS));
        page.add(Box.createVerticalStrut(20));
        
        JPanel textBanner = new JPanel();
        textBanner.setLayout(new BoxLayout(textBanner, BoxLayout.LINE_AXIS));
        textBanner.add(new JLabel("<html><body><strong>Open a grid from your library.</strong></body></html>"));
        textBanner.add(Box.createHorizontalGlue());
        textBanner.setOpaque(false);
        
        page.add(textBanner);
        page.add(Box.createVerticalStrut(30));
        
        JPanel chooserBanner = new JPanel();
        chooserBanner.setLayout(new BoxLayout(chooserBanner, BoxLayout.LINE_AXIS));
        chooserBanner.add(Box.createHorizontalGlue());
        chooserBanner.add(new JLabel("Grid : "));
        chooserBanner.add(Box.createHorizontalStrut(10));
        chooserBanner.add(libraryComboBox);
        chooserBanner.setOpaque(false);
        chooserBanner.setMaximumSize(new Dimension(chooserBanner.getMaximumSize().width, chooserBanner.getMinimumSize().height));
        
        JPanel playBanner = new JPanel();
        playBanner.setLayout(new BoxLayout(playBanner, BoxLayout.LINE_AXIS));
        playBanner.add(Box.createHorizontalGlue());
        playBanner.add(startFromLibraryComboBox);
        playBanner.setOpaque(false);
        playBanner.setMaximumSize(new Dimension(playBanner.getMaximumSize().width, playBanner.getMinimumSize().height));
        
        page.add(chooserBanner);
        page.add(Box.createVerticalStrut(20));
        page.add(playBanner);
        
        page.add(Box.createVerticalStrut(70));
        page.add(Box.createGlue());
        
        JPanel editBanner = new JPanel();
        editBanner.setLayout(new BoxLayout(editBanner, BoxLayout.LINE_AXIS));
        editBanner.add(new JLabel("Edit selected grid  :"));
        editBanner.add(Box.createHorizontalStrut(5));
        editBanner.add(deleteGrid);
        editBanner.add(Box.createHorizontalStrut(5));
        editBanner.add(setGrid);
        editBanner.add(Box.createHorizontalGlue());
        editBanner.setOpaque(false);
        editBanner.setMaximumSize(new Dimension(editBanner.getMaximumSize().width, editBanner.getMinimumSize().height));
        
        page.add(editBanner);
        page.add(Box.createVerticalStrut(10));
        
        JPanel line = new JPanel();
        line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));
        line.add(Box.createHorizontalStrut(10));
        line.add(page);
        line.add(Box.createHorizontalStrut(10));
        
        line.setBackground(BACKGROUND_COLOR);

        
        return line;
    }
    
    private JPanel constructCodingComparatorPanel(){
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.PAGE_AXIS));
        page.add(Box.createVerticalStrut(20));
        page.setOpaque(false);
        
        JPanel textBanner = new JPanel();
        textBanner.setLayout(new BoxLayout(textBanner, BoxLayout.LINE_AXIS));
        textBanner.add(new JLabel("<html><body><strong>Import a grid from Image Coding Comparator.</strong></body></html>"));
        textBanner.add(Box.createHorizontalGlue());
        textBanner.setOpaque(false);
        
        page.add(textBanner);
        page.add(Box.createVerticalStrut(30));
        
        JPanel messageBanner = new JPanel();
        messageBanner.setLayout(new BoxLayout(messageBanner, BoxLayout.LINE_AXIS));
        messageBanner.add(new JLabel("Copy paste the binary output of ICC"));
        messageBanner.add(Box.createHorizontalGlue());
        messageBanner.setOpaque(false);
        
        page.add(messageBanner);
        page.add(Box.createVerticalStrut(5));
        
        codingFlowInput.setPreferredSize(new Dimension(300, 200));
        
        JScrollPane editorScrollPane = new JScrollPane(codingFlowInput);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel scrollPaneBanner = new JPanel();
        scrollPaneBanner.setLayout(new BoxLayout(scrollPaneBanner, BoxLayout.LINE_AXIS));
        scrollPaneBanner.add(Box.createHorizontalGlue());
        scrollPaneBanner.add(editorScrollPane);
        scrollPaneBanner.add(Box.createHorizontalGlue());
        scrollPaneBanner.setOpaque(false);
        
        page.add(scrollPaneBanner);
        page.add(Box.createHorizontalStrut(5));
        
        JPanel startBanner = new JPanel();
        startBanner.setLayout(new BoxLayout(startBanner, BoxLayout.LINE_AXIS));
        startBanner.add(Box.createHorizontalGlue());
        startBanner.add(startFromCodingFlowInput);
        startBanner.add(Box.createHorizontalGlue());
        startBanner.setOpaque(false);
        
        page.add(startBanner);
        page.add(Box.createVerticalStrut(30));
        page.add(Box.createGlue());
        
        JPanel addBanner = new JPanel();
        addBanner.setLayout(new BoxLayout(addBanner, BoxLayout.LINE_AXIS));
        addBanner.add(new JLabel("Add to library"));
        addBanner.add(Box.createHorizontalStrut(5));
        addBanner.add(addToLibraryFromCodingFlowInput);
        addBanner.add(Box.createHorizontalGlue());
        addBanner.setOpaque(false);
        
        page.add(addBanner);
        page.add(Box.createVerticalStrut(10));
        
        JPanel line = new JPanel();
        line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));
        line.add(Box.createHorizontalStrut(10));
        line.add(page);
        line.add(Box.createHorizontalStrut(10));

        line.setBackground(BACKGROUND_COLOR);

        
        return line;
    }
    
    private JPanel constructPicturePanel(){
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.PAGE_AXIS));
        page.add(Box.createVerticalStrut(20));
        page.setOpaque(false);
        
        JPanel textBanner = new JPanel();
        textBanner.setLayout(new BoxLayout(textBanner, BoxLayout.LINE_AXIS));
        textBanner.add(new JLabel("<html><body><strong>Extract a grid from a pixelised picture.</strong></body></html>"));
        textBanner.add(Box.createHorizontalGlue());
        textBanner.setOpaque(false);
        
        page.add(textBanner);
        page.add(Box.createVerticalStrut(30)); 
        
        picName.setPreferredSize(new Dimension(picName.getPreferredSize().width+70, picName.getPreferredSize().height));
        picName.setMaximumSize(picName.getPreferredSize());
        
        JPanel fileBanner = new JPanel();
        fileBanner.setLayout(new BoxLayout(fileBanner, BoxLayout.LINE_AXIS));
        fileBanner.add(openPic);
        fileBanner.add(Box.createHorizontalStrut(5));
        fileBanner.add(picName);
        fileBanner.add(Box.createHorizontalGlue());
        fileBanner.setOpaque(false);
        fileBanner.setMaximumSize(new Dimension(fileBanner.getMaximumSize().width, fileBanner.getMinimumSize().height));
        
        page.add(fileBanner);
        page.add(Box.createVerticalStrut(5));
        
        
        nbOfRows.setMaximumSize(nbOfRows.getPreferredSize());
        nbOfRows.setPreferredSize(nbOfRows.getPreferredSize());
        nbOfColumns.setMaximumSize(nbOfColumns.getPreferredSize());
        nbOfColumns.setPreferredSize(nbOfColumns.getPreferredSize());
        
        JPanel settingsBanner = new JPanel();
        settingsBanner.setLayout(new BoxLayout(settingsBanner, BoxLayout.LINE_AXIS));
        settingsBanner.add(Box.createHorizontalStrut(4));
        settingsBanner.add(new JLabel("Rows :"));
        settingsBanner.add(Box.createHorizontalStrut(5));
        settingsBanner.add(nbOfRows);
        settingsBanner.add(Box.createHorizontalStrut(10));
        settingsBanner.add(new JLabel("columns :"));
        settingsBanner.add(Box.createHorizontalStrut(5));
        settingsBanner.add(nbOfColumns);
        settingsBanner.add(Box.createHorizontalGlue());
        settingsBanner.setOpaque(false);
        settingsBanner.setMaximumSize(new Dimension(settingsBanner.getMaximumSize().width, settingsBanner.getMinimumSize().height));
        
        page.add(settingsBanner);
        page.add(Box.createVerticalStrut(30));
        
        JPanel startBanner = new JPanel();
        startBanner.setLayout(new BoxLayout(startBanner, BoxLayout.LINE_AXIS));
        startBanner.add(Box.createHorizontalGlue());
        startBanner.add(startFromPic);
        startBanner.setOpaque(false);
        
        page.add(startBanner);
        page.add(Box.createVerticalStrut(30));
        page.add(Box.createVerticalGlue());
        
        JPanel importBanner = new JPanel();
        importBanner.setLayout(new BoxLayout(importBanner, BoxLayout.LINE_AXIS));
        importBanner.add(new JLabel("Add to library"));
        importBanner.add(Box.createHorizontalStrut(5));
        importBanner.add(addToLibraryFromPic);
        importBanner.add(Box.createHorizontalGlue());
        importBanner.setOpaque(false);
        importBanner.setMaximumSize(new Dimension(importBanner.getMaximumSize().width, importBanner.getMinimumSize().height));
        
        page.add(importBanner);
        page.add(Box.createVerticalStrut(10));
        
        JPanel line = new JPanel();
        line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));
        line.add(Box.createHorizontalStrut(10));
        line.add(page);
        line.add(Box.createHorizontalStrut(10));

        line.setBackground(BACKGROUND_COLOR);
        
        return line;
    }
    
    private JPanel constructRandomPanel(){
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.PAGE_AXIS));
        page.add(Box.createVerticalStrut(20));
        page.setOpaque(false);
        
        JPanel textBanner = new JPanel();
        textBanner.setLayout(new BoxLayout(textBanner, BoxLayout.LINE_AXIS));
        textBanner.add(new JLabel("<html><body><strong>Let the computer make you a random grid.</strong></body></html>"));
        textBanner.add(Box.createHorizontalGlue());
        textBanner.setOpaque(false);
        
        page.add(textBanner);
        page.add(Box.createVerticalStrut(30));
        
        JPanel messageBanner = new JPanel();
        messageBanner.setLayout(new BoxLayout(messageBanner, BoxLayout.LINE_AXIS));
        messageBanner.add(Box.createHorizontalGlue());
        messageBanner.add(new JLabel("Please select level"));
        messageBanner.add(Box.createHorizontalGlue());
        messageBanner.setOpaque(false);
        
        page.add(messageBanner);
        page.add(Box.createVerticalStrut(5));
        
        JPanel raterBanner = new JPanel();
        raterBanner.setLayout(new BoxLayout(raterBanner, BoxLayout.LINE_AXIS));
        raterBanner.add(Box.createHorizontalGlue());
        raterBanner.add(rater);
        raterBanner.add(Box.createHorizontalGlue());
        raterBanner.setOpaque(false);
        
        page.add(raterBanner);
        page.add(Box.createVerticalGlue());
        
        JPanel playBanner = new JPanel();
        playBanner.setLayout(new BoxLayout(playBanner, BoxLayout.LINE_AXIS));
        playBanner.add(Box.createHorizontalGlue());
        playBanner.add(startFromRandom);
        playBanner.add(Box.createHorizontalGlue());
        playBanner.setOpaque(false);
        
        page.add(Box.createVerticalGlue());
        page.add(playBanner);
        page.add(Box.createVerticalGlue());
        page.add(Box.createVerticalStrut(10));
        
        JPanel line = new JPanel();
        line.setLayout(new BoxLayout(line, BoxLayout.LINE_AXIS));
        line.add(Box.createHorizontalStrut(10));
        line.add(page);
        line.add(Box.createHorizontalStrut(10));
        
        line.setBackground(BACKGROUND_COLOR);
        
        return line;
    }
      
}
