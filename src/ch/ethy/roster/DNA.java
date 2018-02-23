package ch.ethy.roster;

import ch.ethy.roster.Shifts;
import ch.ethy.roster.WeekendShifts;
import ch.ethy.roster.WorkdayShifts;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.Random;

public class DNA {
  private int fitness = 0;
  private Shifts[][] genes;

  DNA(YearMonth month, int personnel) {
    this(month.lengthOfMonth(), personnel);

    EnumSet<DayOfWeek> weekend = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    for(int i = 0; i < month.lengthOfMonth(); i++) {
      this.genes[i] = newRandomDay(personnel, weekend.contains(month.atDay(i+1).getDayOfWeek()));
    }
  }

  private DNA(int daysInMonth, int personnel) {
    this.genes = new Shifts[daysInMonth][personnel];
  }

  public void calcFitness() {
    this.fitness = 0;
  }

  private Shifts[] newRandomDay(int personnel, boolean weekend) {
    Random r = new Random();
    Shifts[] day = new Shifts[personnel];

    Shifts[] shifts = weekend ? WeekendShifts.values() : WorkdayShifts.values();
    for (Shifts shift : shifts) {
      int i;
      do {
        i = r.nextInt(personnel);
      } while (day[i] != null);
      day[i] = shift;
    }

    return day;
  }

  public double getFitness() {
    return fitness;
  }

  public DNA crossover(DNA partner) {
    Random r = new Random();
    DNA child = new DNA(this.genes.length, this.genes[0].length);
    int midpoint = r.nextInt(this.genes.length);

    for(int i = 0; i < this.genes.length; i++) {
      if (i > midpoint) {
        child.genes[i] = this.genes[i];
      } else {
        child.genes[i] = partner.genes[i];
      }
    }

    return child;
  }

  public void mutate(double mutationRate) {
    Random r = new Random();
    for (int i = 0; i < this.genes.length; i++) {
      if (r.nextDouble() < mutationRate) {
        Shifts[] day = this.genes[i];
        int a = r.nextInt(day.length);
        int b = r.nextInt(day.length);
        Shifts shiftA = day[a];
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
          Shifts shift = this.genes[j][i];
          sb.append(String.format("%1$-7s| ", shift == null ? "" : shift.toString()));
        }
      }
      sb.append("\n");
    }

    return sb.toString();
  }
}
