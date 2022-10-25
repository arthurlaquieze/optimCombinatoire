public class Order {
    private int comprimesQuantity;
    private int gelulesQuantity;
    private int sachetsQuantity;
    private int dueDate; // due date as number of days from start

    public Order(int comprimesQuantity, int gelulesQuantity, int sachetsQuantity, int dueDate) {
        this.comprimesQuantity = comprimesQuantity;
        this.gelulesQuantity = gelulesQuantity;
        this.sachetsQuantity = sachetsQuantity;
        this.dueDate = dueDate;
    }

    public Order() {
        /**
         * Generate random order
         */
        this.comprimesQuantity = randomInt(2, 200);
        this.gelulesQuantity = randomInt(2, 200);
        this.sachetsQuantity = randomInt(2, 200);
        this.dueDate = randomInt(2, 500);
    }

    public static int randomInt(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    @Override
    public String toString() {
        return "Order [comprimesQuantity=" + comprimesQuantity + ", gelulesQuantity=" + gelulesQuantity
                + ", sachetsQuantity=" + sachetsQuantity + ", dueDate=" + dueDate + "]";
    }

    public int getDueTime() {
        /**
         * Get due date in minutes from start
         */
        return 24 * 60 * dueDate;
    }

    public int getComprimesQuantity() {
        return comprimesQuantity;
    }

    public int getGelulesQuantity() {
        return gelulesQuantity;
    }

    public int getSachetsQuantity() {
        return sachetsQuantity;
    }

    public int getDueDate() {
        return dueDate;
    }

}
