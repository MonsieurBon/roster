package ch.ethy.roster;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Random;

public class Population {
  private ArrayList<DNA> elements;
  private ArrayList<DNA> matingPool;
  private int generations = 0;
  private boolean finished = false;
  private double mutationRate;
  private String best = "";

  Population(YearMonth month, int personnel, double mutationRate, int populationSize) {
    this.mutationRate = mutationRate;

    this.elements = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      this.elements.add(new DNA(month, personnel));
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
      double n = normFitness * 100;
      for (int i = 0; i < n; i++) {
        this.matingPool.add(element);
      }
    }
  }

  void reproduce() {
    Random r = new Random();
    for (int i = 0; i < elements.size(); i++) {
      int a = r.nextInt(this.matingPool.size());
      int b = r.nextInt(this.matingPool.size());
      DNA partnerA = this.matingPool.get(a);
      DNA partnerB = this.matingPool.get(b);
      DNA child = partnerA.crossover(partnerB);
      child.mutate(this.mutationRate);
      this.elements.set(i, child);
    }
    this.generations++;
  }

  public String getBest() {
    return best;
  }

  public void evaluate() {
    DNA worldRecordHolder = null;

    for (DNA element : this.elements) {
      if (worldRecordHolder == null || worldRecordHolder.getFitness() < element.getFitness()) {
        worldRecordHolder = element;
      }
    }

    this.best = worldRecordHolder.toString();
    if (worldRecordHolder.getFitness() > 0) {
      this.finished = true;
    }
  }

  public boolean isFinished() {
    return finished;
  }

  public int getGenerations() {
    return generations;
  }
}
