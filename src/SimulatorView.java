import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Janela gráfica responsável por exibir o estado atual da simulação.
 * * A classe desenha o campo (grid) da simulação, mostrando cada posição com
 * uma cor correspondente ao tipo de elemento presente (animal, água ou espaço vazio).
 * Também exibe informações como passo atual, estação do ano, arquivo de configuração
 * carregado e velocidade da simulação.
 * * Possui ainda controles para iniciar, pausar, redefinir a simulação,
 * alterar a velocidade e carregar arquivos de configuração.
 */
public class SimulatorView extends JFrame{
    /** Cor padrão usada para locais vazios (terra). */
    private static final Color DEFAULT_EMPTY_COLOR = Color.white;
    
    /** Cor padrão usada para locais com água. */
    private static final Color DEFAULT_WATER_COLOR = new Color(180, 220, 255);

    /** Cor usada para objetos sem cor definida. */
    private static final Color UNKNOWN_COLOR = Color.gray;

    /** Prefixo para o texto do contador de passos. */
    private final String STEP_PREFIX = "Step: ";
    
    /** Prefixo para o texto da estação do ano. */
    private final String SEASON_PREFIX = "Season: ";
    
    /** Prefixo para o texto de população. */
    private final String POPULATION_PREFIX = "Population: ";
    
    /** Rótulos da interface para exibir status e informações. */
    private JLabel stepLabel, seasonLabel, population, configFileLabel, speedLabel;
    
    /** Componente customizado que desenha o grid. */
    private FieldView fieldView;
    
    /** Painel para exibir a legenda de cores das espécies. */
    private JPanel legendPanel;
    
    /** Mapa que armazena cores das espécies. */
    private HashMap<Animal, Color> colors;

    /** Estatísticas do campo. */
    private FieldStats stats;

    /** Carregador de configurações de espécies. */
    private final SpeciesConfigLoader speciesConfig = SpeciesConfigLoader.getInstance();

    /** Referência ao simulador principal. */
    private final Simulator simulator;

    /** A fase da estação atual. */
    private SeasonPhase currentSeasonPhase;

    /**
     * Constrói a janela da simulação com um campo do tamanho especificado.
     *
     * @param height Altura do campo.
     * @param width Largura do campo.
     * @param simulator Instância do simulador que controla a execução.
     */
    public SimulatorView(int height, int width, Simulator simulator){
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
                if (chooser.showOpenDialog(SimulatorView.this) == JFileChooser.APPROVE_OPTION){
                    try{
                        simulator.loadConfiguration(chooser.getSelectedFile().getAbsolutePath());
                        configFileLabel.setText("Config: " + chooser.getSelectedFile().getName());
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(SimulatorView.this, "Error loading config: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        // Aumenta a velocidade
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

        // Diminui a velocidade
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

        // Painel da legenda (espécies e cores)
        legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        contents.add(new JScrollPane(legendPanel), BorderLayout.EAST);

        // Linha de população (não exibida)
        contents.add(population, BorderLayout.SOUTH);
        population.setVisible(false);
        pack();
        setVisible(true);
    }
    
    /**
     * Define a cor a ser utilizada para uma determinada espécie.
     *
     * @param animalClass Classe da espécie.
     * @param color Cor associada.
     */
    public void setColor(Animal animalClass, Color color){
        colors.put(animalClass, color);
    }

    /**
     * Retorna a cor configurada para a espécie informada.
     * Se não houver configuração, tenta usar a cor padrão do arquivo de configuração.
     * Caso contrário, retorna uma cor cinza para indicar desconhecido.
     *
     * @param animalClass Classe da espécie.
     * @return Cor associada à espécie ou cinza se desconhecida.
     */
    private Color getColor(Animal animalClass){
        Color cfg = speciesConfig.getColor(animalClass);

        if(cfg != Color.gray) 
            return cfg;
        Color col = (Color)colors.get(animalClass);
        return col != null ? col : UNKNOWN_COLOR;
    }

    /**
     * Exibe o estado atual da simulação no grid.
     *
     * @param step Passo atual da simulação.
     * @param field O campo contendo os animais.
     * @param seasonName Nome da estação atual.
     */
    public void showStatus(int step, Field field, String seasonName){
        if(!isVisible())
            setVisible(true);

        stepLabel.setText(STEP_PREFIX + step);
        if(seasonName != null){
            seasonLabel.setText(SEASON_PREFIX + seasonName);
        }
        else{
            seasonLabel.setText(SEASON_PREFIX + "-");
        }

        stats.reset();
        fieldView.preparePaint();
        Map<Animal, Integer> counts = new HashMap<Animal, Integer>();
            
        for(int row = 0; row < field.getDepth(); row++){
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

    /**
     * Atualiza o texto que exibe o nível de velocidade atual.
     */
    private void updateSpeedLabel(){
        int level = simulator.getSpeedLevel();
        int ms = 10 + (level - 1) * 66;
        speedLabel.setText("Speed: " + level + " (" + ms + "ms)");
    }
    
    /**
     * Atualiza a legenda lateral exibindo todas as espécies
     * conhecidas e suas contagens atuais no campo.
     *
     * @param counts Mapa contendo as quantidades de cada espécie presente.
     */
    private void updateLegend(Map<Animal, Integer> counts){
        legendPanel.removeAll();

        // Conjunto de todas as espécies a serem exibidas
        Set<Animal> allSpecies = new HashSet<>();
        allSpecies.add(new Fox(false));
        allSpecies.add(new Rabbit(false));
        allSpecies.add(new Fish(false));
        allSpecies.addAll(counts.keySet());
        ArrayList<Animal> keys = new ArrayList<>(allSpecies);

        for(Animal animal : keys){
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

    /**
     * Define a estação atual da simulação.
     *
     * @param phase Fase da estação ativa no momento.
     */
    public void setSeasonPhase(SeasonPhase phase){
        this.currentSeasonPhase = phase;
    }

    /**
     * Retorna a cor usada para células vazias conforme a estação atual.
     *
     * @return Cor da célula vazia para a estação ou cor padrão.
     */
    private Color getSeasonalEmptyColor(){
        if(currentSeasonPhase != null) 
            return currentSeasonPhase.getEmptyColor();
        return DEFAULT_EMPTY_COLOR;
    }

    /**
     * Retorna a cor usada para células de água conforme a estação atual.
     *
     * @return Cor da água para a estação ou cor padrão.
     */
    private Color getSeasonalWaterColor(){
        if(currentSeasonPhase != null) 
            return currentSeasonPhase.getWaterColor();
        return DEFAULT_WATER_COLOR;
    }

    /**
     * Verifica se a simulação ainda é viável, ou seja,
     * se mais de uma espécie continua viva.
     *
     * @param field O campo sendo avaliado.
     * @return true se ainda houver diversidade biológica.
     */
    public boolean isViable(Field field){
        return stats.isViable(field);
    }

    /**
     * Componente gráfico interno responsável por desenhar o campo
     * da simulação em forma de grade colorida.
     */
    private class FieldView extends JPanel{
        /** Fator de escala para o tamanho da visualização do grid. */
        private final int GRID_VIEW_SCALING_FACTOR = 10;

        /** Dimensões do grid (largura e altura) em número de células. */
        private int gridWidth, gridHeight;
        
        /** Escala de desenho nos eixos X e Y. */
        private int xScale, yScale;
        
        /** Tamanho atual do componente. */
        Dimension size;
        
        /** Contexto gráfico utilizado para desenho. */
        private Graphics g;
        
        /** Buffer de imagem onde o campo é desenhado antes de ser exibido. */
        private Image fieldImage;

        /**
         * Constrói o componente responsável por exibir o campo.
         *
         * @param height Altura (número de linhas) do grid.
         * @param width  Largura (número de colunas) do grid.
         */
        public FieldView(int height, int width){
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Informa ao gerenciador de layout o tamanho desejado para o painel.
         *
         * @return Dimensão recomendada baseada no fator de escala da grade.
         */
        public Dimension getPreferredSize(){
            return new Dimension(
                gridWidth * GRID_VIEW_SCALING_FACTOR,
                gridHeight * GRID_VIEW_SCALING_FACTOR
            );
        }
        
        /**
         * Prepara a área de pintura, recriando a imagem caso o painel
         * tenha sido redimensionado.
         */
        public void preparePaint(){
            if(! size.equals(getSize())){
                size = getSize();
                fieldImage = createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1){
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1){
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Desenha um retângulo representando o conteúdo de uma célula
         * da simulação na cor especificada.
         *
         * @param x     Coluna da célula.
         * @param y     Linha da célula.
         * @param color Cor a ser aplicada na célula.
         */
        public void drawMark(int x, int y, Color color){
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * Re-renderiza visualmente o painel desenhando a imagem interna
         * previamente montada no buffer.
         *
         * @param g Contexto gráfico do Swing.
         */
        public void paintComponent(Graphics g){
            if(fieldImage != null){
                g.drawImage(fieldImage, 0, 0, null);
            }
        }
    }
}