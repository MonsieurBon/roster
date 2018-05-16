package ch.ethy.roster;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DNA {
  private int fitness = 0;
  private int invalidShifts = 0;
  private Shift[][] genes;

  DNA(int daysInMonth, int personnel) {
    this.genes = new Shift[daysInMonth][personnel];

    for(int i = 0; i < daysInMonth; i++) {
      this.genes[i] = newRandomDay(personnel);
    }
  }

  void updateFitness() {
    if (this.fitness == 0) {
      this.fitness = getRepetitionScore();
      this.invalidShifts = numberOfInvalidShifts();
    }

    this.fitness -= this.invalidShifts;

    if (this.fitness < 1) {
      this.fitness = 1;
    }
  }

  private int getRepetitionScore() {
    int score = 0;
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
    return score;
  }

  private int numberOfInvalidShifts() {
    int invalidShifts = 0;

    // Check validity for all personell
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

      String shiftString = shiftStringBuilder.toString();

      Matcher matcher = Shift.getPattern().matcher(shiftString);

      if (!matcher.matches()) {
        invalidShifts++;
      }
    }

    return invalidShifts;
  }

  private int findViolationsInShiftString(String shiftString) {
    Pattern pattern = Shift.getPattern();

    int start = 0;
    int end = 1;
    int violations = 0;

    while(end <= shiftString.length()) {
      String subTester = shiftString.substring(start, end);
      Matcher matcher = pattern.matcher(subTester);
      if (matcher.matches()) {
        end++;
      } else {
        violations++;
        start = end-1;
      }
    }

    return violations;
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

    return child;
  }

  void mutate(double mutationRate) {
    Random r = new Random();
    for (int i = 0; i < this.genes.length; i++) {
      if (r.nextDouble() < mutationRate) {
        this.genes[i] = newRandomDay(this.genes[i].length);
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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof DNA) {
      DNA other = (DNA) obj;

      Shift[][] otherGenes = other.genes;
      Shift[][] myGenes = this.genes;

      return Arrays.deepEquals(myGenes, otherGenes);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(this.genes);
  }
}
