package ch.ethy.roster;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;

public class DNA {
  private int fitness = 0;
  private Shift[][] genes;

  DNA(int daysInMonth, int personnel) {
    this.genes = new Shift[daysInMonth][personnel];

    for(int i = 0; i < daysInMonth; i++) {
      this.genes[i] = newRandomDay(personnel);
    }
  }

  void updateFitness() {
    int score = 1;
    if (this.fitness == 0) {
      // Repeating shifts are good!
      for (int i = 0; i < this.genes[0].length; i++) {
        Shift lastShift = null;
        int repetitions = 0;
        for (Shift[] gene : this.genes) {
          Shift currentShift = gene[i];
          if (lastShift != null) {
            if (lastShift == currentShift) {
              repetitions++;

              if (repetitions >= 7) {
                score = score - (repetitions - 7);
              } else {
                score = score + repetitions;
              }
            } else {
              lastShift = currentShift;
              if (currentShift == null) {
                repetitions = 0;
              } else {
                repetitions++;
              }
            }
          } else {
            lastShift = currentShift;
            repetitions = 0;
          }
        }
      }

      this.fitness = score < 1 ? 1 : score;
    }

    if (this.fitness > 1) {
      validateRoster();
    }
  }

  private void validateRoster() {
    // Check validity for all days
//    for (Shift[] gene : this.genes) {
//      // Check shifts multiple in gene
//      if(containsDuplicateShifts(gene)){
//        this.fitness = 1;
//        return;
//      }
//
//      if (!containsAllShifts(gene)) {
//        this.fitness = 1;
//        return;
//      }
//    }

    // Check validity for all personell
    int matchCount = 0;
    for (int i = 0; i < this.genes[0].length; i++) {
      StringBuilder shiftStringBuilder = new StringBuilder();

      for (Shift[] gene : this.genes) {
        Shift shift = gene[i];
        if (shift == null) {
          shiftStringBuilder.append(" ");
        } else {
          shiftStringBuilder.append(shift.getId());
        }
      }

      Matcher matcher = Shift.getPattern().matcher(shiftStringBuilder.toString());

      if (matcher.matches()) {
        matchCount++;
        this.fitness += matchCount;
      }
    }
  }

  private boolean containsAllShifts(Shift[] gene) {
    return Arrays.asList(gene).containsAll(Arrays.asList(Shift.values()));
  }

  private boolean containsDuplicateShifts(Shift[] gene) {
    Set<Shift> foundShifts = new HashSet<>();

    for (Shift shift : gene) {
      if (shift != null) {
        if (foundShifts.contains(shift)) {
          return true;
        } else {
          foundShifts.add(shift);
        }
      }
    }

    return false;
  }

  private Shift[] newRandomDay(int personnel) {
    Random r = new Random();
    Shift[] day = new Shift[personnel];

    Shift[] shifts = Shift.values();
    for (Shift shift : shifts) {
      int i;
      do {
        i = r.nextInt(personnel);
      } while (day[i] != null);
      day[i] = shift;
    }

    return day;
  }

  int getFitness() {
    return fitness;
  }

  DNA crossover(DNA partner) {
    Random r = new Random();
    DNA child = new DNA(this.genes.length, this.genes[0].length);

    double useMine = ((double)this.fitness) / (this.fitness + partner.fitness);

    for (int i = 0; i < this.genes.length; i++) {
      double rnd = r.nextDouble();
      child.genes[i] = rnd <= useMine ? this.genes[i] : partner.genes[i];
    }

//    for (int i = 0; i < this.genes.length; i++) {
//      Shift[] gene = this.genes[i];
//      for (int j = 0; j < gene.length; j++) {
//        double rnd = r.nextDouble();
//        child.genes[i][j] = rnd <= useMine ? this.genes[i][j] : partner.genes[i][j];
//      }
//    }

    return child;
  }

  void mutate(double mutationRate) {
    Random r = new Random();
    for (int i = 0; i < this.genes.length; i++) {
      if (r.nextDouble() < mutationRate) {
        Shift[] day = this.genes[i];
        int a = r.nextInt(day.length);
        int b = r.nextInt(day.length);
        Shift shiftA = day[a];
        day[a] = day[b];
        day[b] = shiftA;
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for (int i = -2; i < this.genes[0].length; i++) {
      for (int j = 0; j < this.genes.length; j++) {
        if (i == -2) {
          String dayInMonth = Integer.toString(j + 1);
          sb.append(String.format("%1$-7s| ", dayInMonth));
        } else if (i == -1) {
          String separator = "-------|-";
          sb.append(separator);
        } else {
          Shift shift = this.genes[j][i];
          sb.append(String.format("%1$-7s| ", shift == null ? "" : shift.toString()));
        }
      }
      sb.append("\n");
    }

    return sb.toString();
  }
}
