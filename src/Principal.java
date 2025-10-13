public class Principal{
  public static void main(String[] args){
    Simulator simulator = new Simulator();
    simulator.configureLake(25, 25, 25, 30); // centro (linha,coluna), altura, largura
    simulator.runLongSimulation();
    //simulator.simulate(300);
  }
}