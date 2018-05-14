package ch.ethy.roster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class Population {
  private List<DNA> elements;
  private List<DNA> matingPool;
  private int generations = 0;
  private boolean finished = false;
  private double mutationRate;
  private int worldRecord = 0;
  private int generationsSinceWorldRecord = 0;
  private int populationSize;

  Population(int daysInMonth, int personnel, double mutationRate, int populationSize) {
    this.mutationRate = mutationRate;
    this.populationSize = populationSize;

    this.elements = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      this.elements.add(new DNA(daysInMonth, personnel));
    }

    this.calcFitness();
  }

  void calcFitness() {
    for (DNA element : this.elements) {
      element.calcFitness();
    }
  }

  void naturalSelection() {
    this.matingPool = new ArrayList<>(this.elements.size());

    double maxFitness = 0;
    for (DNA element : this.elements) {
      if (element.getFitness() > maxFitness) {
        maxFitness = element.getFitness();
      }
    }

    for (DNA element : this.elements) {
      double normFitness = 0.0;
      if (maxFitness > 0) {
        normFitness = element.getFitness() / maxFitness;
      }
      double n = Math.ceil(normFitness * 100);
      for (int i = 0; i < n; i++) {
        this.matingPool.add(element);
      }
    }
  }

  void reproduce() {
    Random r = new Random();
    for (int i = 0; i < this.populationSize; i++) {
      int a = r.nextInt(this.matingPool.size());
      int b = r.nextInt(this.matingPool.size());
      DNA partnerA = this.matingPool.get(a);
      DNA partnerB = this.matingPool.get(b);
      DNA child = partnerA.crossover(partnerB);
      child.mutate(this.mutationRate);
      this.elements.add(child);
    }
    this.generations++;
  }

  String getBest() {
    return this.elements.get(0).toString();
  }

  void evaluate() {
    this.elements.sort((o1, o2) -> o2.getFitness() - o1.getFitness());
    this.elements = new ArrayList<>(this.elements.subList(0, this.populationSize));

    DNA currentBest = this.elements.get(0);
    int currentRecord = currentBest.getFitness();
    if (currentRecord > worldRecord) {
      this.worldRecord = currentRecord;
      this.generationsSinceWorldRecord = 0;
    } else {
      this.generationsSinceWorldRecord++;
    }

    if (this.generationsSinceWorldRecord > 100) {
      this.finished = true;
    }
  }

  boolean isFinished() {
    return finished;
  }

  int getGenerations() {
    return generations;
  }

  int getWorldRecord() {
    return worldRecord;
  }
}
