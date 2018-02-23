package ch.ethy.roster;


import java.time.YearMonth;

public class Main {
  public static void main(String[] args) {
    int populationSize = 200;
    double mutationRate = 0.01;

    Population population = new Population(YearMonth.of(2018, 3), 10, mutationRate, populationSize);
    population.evaluate();

    System.out.println("Current best roster:");
    System.out.print(population.getBest());

    while(!population.isFinished()) {
      population.naturalSelection();
      population.reproduce();
      population.calcFitness();
      population.evaluate();

      System.out.println("Current best roster:");
      System.out.print(population.getBest());
    }

    System.out.println("Generations: " + population.getGenerations());

    //    this.personnel = new Person[10];
//    this.personnel[0] = new Person("Keslie", "Pavlov");
//    this.personnel[1] = new Person("Ulric", "Vondrasek");
//    this.personnel[2] = new Person("Cybill", "Bellam");
//    this.personnel[3] = new Person("Laurette", "Glendinning");
//    this.personnel[4] = new Person("Stavros", "Lakenden");
//    this.personnel[5] = new Person("Cad", "Dursley");
//    this.personnel[6] = new Person("Herschel", "Soppett");
//    this.personnel[7] = new Person("Freeman", "Quick");
//    this.personnel[8] = new Person("Niels", "Skellorne");
//    this.personnel[9] = new Person("Katee", "Inkpin");

  }
}
