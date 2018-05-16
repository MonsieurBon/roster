package ch.ethy.roster;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum Shift {
  EARLY('E') {
    @Override
    public String getShiftPattern() {
      return "E{1,10}(?!E)";
    }
  },
  MIDDLE('M') {
    @Override
    public String getShiftPattern() {
      return "M{1,10}(?![ME])";
    }
  },
  NIGHT('N') {
    @Override
    public String getShiftPattern() {
      return "N{1,6}(?=$)|N{7}(?=( *$| {4}))";
    }
  };

  private static final Pattern PATTERN;

  static {
    Shift[] allShifts = Shift.values();

    String regexStart = "^ *(((?=\\w{1,10}( |$))(";
    String shiftPatterns = Arrays.asList(allShifts).stream().map(Shift::getShiftPattern).collect(Collectors.joining("|"));
    String regexEnd = ")+) *)*$";

    PATTERN = Pattern.compile(regexStart + shiftPatterns + regexEnd);
  }

  private final char id;

  Shift(char id) {
    this.id = id;
  }

  public char getId() {
    return id;
  }

  public abstract String getShiftPattern();

  public static int getGlobalMax() {
    return 10;
  }

  public static Pattern getPattern() {
    return PATTERN;
  }
}
