package ch.ethy.roster;

public enum Shift {
  EARLY,
  MIDDLE {
    @Override
    public Shift[] mayNotBeFollowedBy() {
      return new Shift[]{EARLY};
    }
  },
  NIGHT {
    @Override
    public int getMaxRepetitions() {
      return 7;
    }

    @Override
    public Shift[] mayNotBeFollowedBy() {
      return new Shift[]{EARLY, MIDDLE};
    }
  };

  public int getMaxRepetitions() {
    return 10;
  }

  public Shift[] mayNotBeFollowedBy() {
    return new Shift[]{};
  }

  public static int getGlobalMax() {
    return 10;
  }
}
