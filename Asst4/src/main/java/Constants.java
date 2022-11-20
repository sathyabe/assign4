public class Constants {
    private static final String FILE_PATH = "./src/main/java/";
    static final String RANDOM_PACKET = FILE_PATH + "RandomPackets.txt";
    static final String ROUTING_TABLE = FILE_PATH + "RoutingTable.txt";
    static final String ROUTING_OP = FILE_PATH + "RoutingOutput.txt";
    static final String LOOPBACK_ADDR = "127.0.0.1";
    static final String DEFAULT_ADDR = "0.0.0.0";
    static final String[] MASKING_ADDR = new String[]{"255.255.255.255", "255.255.255.0", "255.255.0.0", "255.0.0.0"};
}
