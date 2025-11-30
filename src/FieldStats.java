import java.util.HashMap;
import java.util.Iterator;

/**
 * Classe responsável por coletar e fornecer estatísticas sobre o estado do campo.
 * Ela é flexível, criando contadores dinamicamente para cada tipo de animal encontrado.
 */
public class FieldStats {
    /** Mapa que associa cada espécie de animal ao seu respectivo contador. */
    private HashMap<Animal, Counter> counters;

    /** Indica se os valores atuais dos contadores estão válidos ou precisam ser atualizados. */
    private boolean countsValid;

    /**
     * Construtor padrão da classe de estatísticas do campo.
     * Inicializa o mapa de contadores e marca as estatísticas como válidas.
     */
    public FieldStats() {
        counters = new HashMap<>();
        countsValid = true;
    }

    /**
     * Invalida as estatísticas atuais e reseta todos os contadores para zero.
     */
    public void reset() {
        countsValid = false;
        Iterator<Animal> keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter counter = counters.get(keys.next());
            counter.reset();
        }
    }

    /**
     * Incrementa o contador da espécie correspondente.
     * Caso a espécie ainda não possua um contador, um novo é criado.
     * @param animalClass O animal cuja espécie será contabilizada.
     */
    public void incrementCount(Animal animalClass) {
        Counter counter = counters.get(animalClass);
        if (counter == null) {
            counter = new Counter(getDisplayName(animalClass));
            counters.put(animalClass, counter);
        }
        counter.increment();
    }

    /**
     * Retorna o nome a ser exibido para a classe do animal.
     * Realiza pluralização simples para algumas espécies.
     * @param animalClass O animal do qual se deseja o nome de exibição.
     * @return O nome formatado (ex: "foxes" para "fox").
     */
    private String getDisplayName(Animal animalClass) {
        String simple = animalClass.getClass().getSimpleName().toLowerCase();
        if ("fox".equals(simple)) return "foxes";
        if ("rabbit".equals(simple)) return "rabbits";
        if ("fish".equals(simple)) return "fishes";
        return simple;
    }

    /** Marca que a contagem de animais foi finalizada e está atualizada. */
    public void countFinished() {
        countsValid = true;
    }

    /**
     * Verifica se a simulação ainda é viável, ou seja, se mais de uma espécie está viva.
     * @param field O campo atual da simulação.
     * @return true se houver mais de uma espécie com contagem maior que zero.
     */
    public boolean isViable(Field field) {
        int nonZero = 0;
        if (!countsValid) {
            generateCounts(field);
        }
        Iterator<Animal> keys = counters.keySet().iterator();
        while (keys.hasNext()) {
            Counter info = counters.get(keys.next());
            if (info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }

    /**
     * Gera as contagens de todas as espécies presentes no campo.
     * A contagem só é atualizada quando solicitada.
     * @param field O campo onde os animais estão localizados.
     */
    private void generateCounts(Field field) {
        reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Animal animal = field.getObjectAt(row, col);
                if (animal != null) {
                    incrementCount(animal);
                }
            }
        }
        countsValid = true;
    }
}