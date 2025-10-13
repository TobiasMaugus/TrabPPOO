public class Principal{
  public static void main(String[] args){
    Simulator simulator = new Simulator();
    simulator.addLake(20, 20, 10, 12);
    simulator.addLake(30, 35, 8, 10);
    simulator.runLongSimulation();
  }
}