package models.profiles;

public class Age implements Comparable<Age> {
    private int year;
    private int month;

    public Age(String notation) {
        String[] splitStr = notation.split(";");
        this.year = Integer.parseInt(splitStr[0]);
        this.month = Integer.parseInt(splitStr[1]);
    }

    public boolean isInAgePeriod(String agePeriod) {
        String[] splitStr = agePeriod.split("-");
        Age lower = new Age(splitStr[0]);
        Age upper = new Age(splitStr[1]);
        return this.compareTo(lower) >= 0 && this.compareTo(upper) <= 0;
    }

    @Override
    public String toString() {
        return year + ";" + month;
    }

    @Override
    public int compareTo(Age o) {
        if (this.year > o.year) {
            return 1;
        } else if (this.year < o.year) {
            return -1;
        } else {
            return Integer.compare(this.month, o.month);
        }
    }
}
