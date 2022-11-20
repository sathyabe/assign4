public final class Routing{
    private final String destAddr;
    private final int masked;
    private final String nextHop;
    private final String outInterface;
    public Routing(final String destAddr,
                   final int masked,
                   final String nextHop,
                   final String outInterface) {
        this.destAddr = destAddr;
        this.masked = masked;
        this.nextHop = nextHop;
        this.outInterface = outInterface;
    }
    public String getdestAddr() {
        return destAddr;
    }
    public int getmasked() {
        return masked;
    }
    public String getnextHop() {
        return nextHop;
    }
    public String getoutInterface() {
        return outInterface;
    }
    public static void main(String[] args) {
            new Simulation().execute();
        }
}