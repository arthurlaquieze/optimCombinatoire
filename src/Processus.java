public class Processus {
    int duration; // time to process one unit.
    int step; // step it is in the process
    String name;

    public Processus(int duration, int step, String name) {
        this.duration = duration;
        this.step = step;
        this.name = name;
    }

    public Processus(int duration, int step) {
        this.duration = duration;
        this.step = step;
    }

    @Override
    public String toString() {
        return "Processus [duration=" + duration + ", step=" + step + ", name=" + name + "]";
    }

}
