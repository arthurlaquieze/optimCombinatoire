public class Processus {
    private int duration; // time to process one unit. in minutes
    private int step; // step it is in the process
    private String name;

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

    public int getDuration() {
        return duration;
    }

    public int getStep() {
        return step;
    }

    public String getName() {
        return name;
    }

}
