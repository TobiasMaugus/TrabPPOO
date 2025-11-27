import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-23
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color DEFAULT_EMPTY_COLOR = Color.white;
    private static final Color DEFAULT_WATER_COLOR = new Color(180, 220, 255);

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String SEASON_PREFIX = "Season: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, seasonLabel, population, configFileLabel, speedLabel;
    private FieldView fieldView;
    private JPanel legendPanel;
    
    // A map for storing colors for participants in the simulation
    private HashMap<Animal, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private final SpeciesConfigLoader speciesConfig;
    private final Simulator simulator;
    private SeasonPhase currentSeasonPhase;

    /**
     * Create a view of the given width and height.
     */
    public SimulatorView(int height, int width, SpeciesConfigLoader speciesConfig, Simulator simulator){
        this.speciesConfig = speciesConfig;
        this.simulator = simulator;
        stats = new FieldStats();
        colors = new HashMap<Animal, Color>();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setTitle("Simulação de predador/presa");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        seasonLabel = new JLabel(SEASON_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        configFileLabel = new JLabel("Configurator: (default)", JLabel.CENTER);
        speedLabel = new JLabel("Speed: 1 (10ms)", JLabel.CENTER);
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        JPanel topPanel = new JPanel(new GridLayout(5,1));
        JPanel statusPanel = new JPanel(new GridLayout(1,2));
        statusPanel.add(stepLabel);
        statusPanel.add(seasonLabel);
        JPanel configPanel = new JPanel(new GridLayout(1,1));
        configPanel.add(configFileLabel);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadConfigBtn = new JButton("LOAD CONFIG");
        JButton playPause = new JButton("PLAY");
        JButton resetBtn = new JButton("RESET");
        
        // Botões de velocidade
        JButton speedUp1 = new JButton("+1");
        JButton speedUp5 = new JButton("+5");
        JButton speedDown1 = new JButton("-1");
        JButton speedDown5 = new JButton("-5");
        playPause.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(simulator.isRunning()) {
                    simulator.pauseSimulation();
                    playPause.setText("PLAY");
                } else {
                    simulator.startSimulation();
                    playPause.setText("PAUSE");
                }
            }
        });
        resetBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                simulator.pauseSimulation();
                simulator.reset();
                playPause.setText("PLAY");
            }
        });
        loadConfigBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files", "txt"));
                if (chooser.showOpenDialog(SimulatorView.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        simulator.loadConfiguration(chooser.getSelectedFile().getAbsolutePath());
                        configFileLabel.setText("Config: " + chooser.getSelectedFile().getName());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(SimulatorView.this, "Error loading config: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        // ActionListeners para botões de velocidade
        speedUp1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                simulator.increaseSpeed();
                updateSpeedLabel();
            }
        });
        speedUp5.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                simulator.increaseSpeedBy5();
                updateSpeedLabel();
            }
        });
        speedDown1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                simulator.decreaseSpeed();
                updateSpeedLabel();
            }
        });
        speedDown5.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                simulator.decreaseSpeedBy5();
                updateSpeedLabel();
            }
        });
        controls.add(loadConfigBtn);
        controls.add(playPause);
        controls.add(resetBtn);
        controls.add(speedUp1);
        controls.add(speedUp5);
        controls.add(speedDown1);
        controls.add(speedDown5);
        
        JPanel speedPanel = new JPanel(new GridLayout(1,1));
        speedPanel.add(speedLabel);
        
        topPanel.add(statusPanel);
        topPanel.add(configPanel);
        topPanel.add(speedPanel);
        topPanel.add(controls);
        contents.add(topPanel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        // Legenda na lateral direita
        legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        contents.add(new JScrollPane(legendPanel), BorderLayout.EAST);
        // Oculta a linha de população textual na base
        contents.add(population, BorderLayout.SOUTH);
        population.setVisible(false);
        pack();
        setVisible(true);
    }
    
    /**
     * Define a color to be used for a given class of animal.
     */
    public void setColor(Animal animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * Define a color to be used for a given class of animal.
     */
    private Color getColor(Animal animalClass)
    {
        Color cfg = speciesConfig.getColor(animalClass);
        if(cfg != Color.gray) return cfg;
        Color col = (Color)colors.get(animalClass);
        return col != null ? col : UNKNOWN_COLOR;
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param stats Status of the field to be represented.
     */
    public void showStatus(int step, Field field, String seasonName)
    {
        if(!isVisible())
            setVisible(true);

        stepLabel.setText(STEP_PREFIX + step);
        if(seasonName != null) {
            seasonLabel.setText(SEASON_PREFIX + seasonName);
        } else {
            seasonLabel.setText(SEASON_PREFIX + "-");
        }

        stats.reset();
        fieldView.preparePaint();
        Map<Animal, Integer> counts = new HashMap<Animal, Integer>();
            
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Animal animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal);
                    fieldView.drawMark(col, row, getColor(animal));
                    Integer c = counts.get(animal);
                    counts.put(animal, c == null ? 1 : c + 1);
                }
                else if(field.isWater(row, col)) {
                    fieldView.drawMark(col, row, getSeasonalWaterColor());
                }
                else {
                    fieldView.drawMark(col, row, getSeasonalEmptyColor());
                }
            }
        }
        stats.countFinished();

        // Atualiza legenda lateral
        updateLegend(counts);
        fieldView.repaint();
    }

    private void updateSpeedLabel() {
        int level = simulator.getSpeedLevel();
        int ms = 10 + (level - 1) * 66;
        speedLabel.setText("Speed: " + level + " (" + ms + "ms)");
    }
    
    private void updateLegend(Map<Animal, Integer> counts) {
        legendPanel.removeAll();
        // Sempre mostra todas as espécies conhecidas, mesmo com contagem 0
        Set<Animal> allSpecies = new HashSet<>();
        allSpecies.add(new Fox(false));
        allSpecies.add(new Rabbit(false));
        allSpecies.add(new Fish(false));
        allSpecies.addAll(counts.keySet());
        ArrayList<Animal> keys = new ArrayList<>(allSpecies);

        for(Animal animal : keys) {
            int count = counts.containsKey(animal) ? counts.get(animal).intValue() : 0;
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel swatch = new JPanel();
            swatch.setBackground(getColor(animal));
            swatch.setPreferredSize(new Dimension(16,16));
            swatch.setMinimumSize(new Dimension(16,16));
            swatch.setMaximumSize(new Dimension(16,16));
            row.add(swatch);
            row.add(Box.createHorizontalStrut(8));
            JLabel label = new JLabel(animal.getClass().getSimpleName() + " - " + count);
            row.add(label);
            legendPanel.add(row);
        }
        legendPanel.revalidate();
        legendPanel.repaint();
    }

    public void setSeasonPhase(SeasonPhase phase) {
        this.currentSeasonPhase = phase;
    }

    private Color getSeasonalEmptyColor() {
        if(currentSeasonPhase != null) return currentSeasonPhase.getEmptyColor();
        return DEFAULT_EMPTY_COLOR;
    }

    private Color getSeasonalWaterColor() {
        if(currentSeasonPhase != null) return currentSeasonPhase.getWaterColor();
        return DEFAULT_WATER_COLOR;
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 10;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }
        
        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                g.drawImage(fieldImage, 0, 0, null);
            }
        }
    }
}
