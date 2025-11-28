/**
 * Ciclo de estações configurável.
 */
public class SeasonCycle{
    private final SeasonPhase[] phases;
    private int totalDuration;

    public SeasonCycle(SeasonPhase[] phases){
        this.phases = phases;
        int sum = 0;
        for(SeasonPhase p : phases) 
            sum += p.getDurationSteps();
        this.totalDuration = sum;
    }

    public SeasonPhase getPhaseAtStep(int globalStep){
        if(totalDuration == 0 || phases.length == 0) 
            return null;
        int stepInCycle = globalStep % totalDuration;
        int acc = 0;
        for(SeasonPhase p : phases){
            acc += p.getDurationSteps();
            if(stepInCycle < acc) return p;
        }
        return phases[phases.length - 1];
    }
}


