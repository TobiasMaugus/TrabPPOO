import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;

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
    private JLabel stepLabel, seasonLabel, population;
    private FieldView fieldView;
    private JPanel legendPanel;
    
    // A map for storing colors for participants in the simulation
    private HashMap colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private final SpeciesConfig speciesConfig;
    private SeasonPhase currentSeasonPhase;

    /**
     * Create a view of the given width and height.
     */
    public SimulatorView(int height, int width, SpeciesConfig speciesConfig, Simulator simulator)
    {
        this.speciesConfig = speciesConfig;
        stats = new FieldStats();
        colors = new HashMap();

        setTitle("Predator/Prey Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        seasonLabel = new JLabel(SEASON_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        setSize(1000, 1000);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        JPanel topPanel = new JPanel(new GridLayout(3,1));
        JPanel statusPanel = new JPanel(new GridLayout(1,2));
        statusPanel.add(stepLabel);
        statusPanel.add(seasonLabel);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton playPause = new JButton("PLAY");
        JButton resetBtn = new JButton("RESET");
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
        controls.add(playPause);
        controls.add(resetBtn);
        topPanel.add(statusPanel);
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
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * Define a color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
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
        java.util.Map<Class, Integer> counts = new java.util.HashMap<Class, Integer>();
            
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                    Integer c = counts.get(animal.getClass());
                    counts.put(animal.getClass(), c == null ? 1 : c + 1);
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

    private void updateLegend(java.util.Map<Class, Integer> counts) {
        legendPanel.removeAll();
        // Sempre mostra todas as espécies conhecidas, mesmo com contagem 0
        java.util.Set<Class> allSpecies = new java.util.HashSet<Class>();
        allSpecies.add(Fox.class);
        allSpecies.add(Rabbit.class);
        allSpecies.add(Fish.class);
        allSpecies.addAll(counts.keySet());
        java.util.List<Class> keys = new java.util.ArrayList<Class>(allSpecies);
        java.util.Collections.sort(keys, new java.util.Comparator<Class>(){
            public int compare(Class a, Class b) { return a.getSimpleName().compareTo(b.getSimpleName()); }
        });
        for(Class cls : keys) {
            int count = counts.containsKey(cls) ? counts.get(cls).intValue() : 0;
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel swatch = new JPanel();
            swatch.setBackground(getColor(cls));
            swatch.setPreferredSize(new Dimension(16,16));
            swatch.setMinimumSize(new Dimension(16,16));
            swatch.setMaximumSize(new Dimension(16,16));
            row.add(swatch);
            row.add(Box.createHorizontalStrut(8));
            JLabel label = new JLabel(cls.getSimpleName() + " - " + count);
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
