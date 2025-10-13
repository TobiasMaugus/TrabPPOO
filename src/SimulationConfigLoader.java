import java.io.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Carregador de configuração de simulação a partir de arquivo .txt
 */
public class SimulationConfigLoader {
    
    public static SimulationConfig loadFromFile(String filePath) throws IOException {
        SimulationConfig config = new SimulationConfig();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // ignora linhas vazias e comentários
                
                if (line.startsWith("GRID_WIDTH=")) {
                    config.setGridWidth(Integer.parseInt(line.substring(11)));
                } else if (line.startsWith("GRID_HEIGHT=")) {
                    config.setGridHeight(Integer.parseInt(line.substring(12)));
                } else if (line.startsWith("MAX_STEPS=")) {
                    config.setMaxSteps(Integer.parseInt(line.substring(10)));
                } else if (line.startsWith("SEASON=")) {
                    // Formato: SEASON=name,duration,r,g,b,r,g,b (empty color, water color)
                    String[] parts = line.substring(7).split(",");
                    if (parts.length >= 8) {
                        String name = parts[0];
                        int duration = Integer.parseInt(parts[1]);
                        Color emptyColor = new Color(
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4])
                        );
                        Color waterColor = new Color(
                            Integer.parseInt(parts[5]),
                            Integer.parseInt(parts[6]),
                            Integer.parseInt(parts[7])
                        );
                        config.addSeason(new SimulationConfig.SeasonConfig(name, duration, emptyColor, waterColor));
                    }
                } else if (line.startsWith("LAKE=")) {
                    // Formato: LAKE=centerRow,centerCol,height,width
                    String[] parts = line.substring(5).split(",");
                    if (parts.length >= 4) {
                        int centerRow = Integer.parseInt(parts[0]);
                        int centerCol = Integer.parseInt(parts[1]);
                        int height = Integer.parseInt(parts[2]);
                        int width = Integer.parseInt(parts[3]);
                        config.addLake(new SimulationConfig.LakeConfig(centerRow, centerCol, height, width));
                    }
                } else if (line.startsWith("SPECIES_RATE=")) {
                    // Formato: SPECIES_RATE=speciesName,seasonName,breedingRate,predationSusceptibility,foodAvailability
                    String[] parts = line.substring(13).split(",");
                    if (parts.length >= 5) {
                        String speciesName = parts[0];
                        String seasonName = parts[1];
                        double breedingRate = Double.parseDouble(parts[2]);
                        double predationSusceptibility = Double.parseDouble(parts[3]);
                        double foodAvailability = Double.parseDouble(parts[4]);
                        config.addSpeciesRate(new SimulationConfig.SpeciesRateConfig(speciesName, seasonName, breedingRate, predationSusceptibility, foodAvailability));
                    }
                }
            }
        } finally {
            reader.close();
        }
        
        return config;
    }
}
