import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Simulation {
    private void routingDecisionOutput(final String msg) {
        try (final FileWriter routingOpFileReader = new FileWriter(Constants.ROUTING_OP, true)) {
            routingOpFileReader.append(msg);
            routingOpFileReader.append("\n");
        } catch (final IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    private String getRoutingmsg(final String newAddress,
                                 final String randomDestAddr,
                                 final Map<String, Routing> routingTableMap) {
        String msg = "";
        if (routingTableMap.containsKey(newAddress)) {
            final Routing routingEntry = routingTableMap.get(newAddress);
            if (routingEntry.getnextHop().equals("-")) {
                msg = String.format("%s will be forwarded on the directly connected network on outInterface %s.",
                        randomDestAddr, routingEntry.getoutInterface());
            } else {
                msg = String.format("%s will be forwarded to %s out on outInterface %s.",
                        randomDestAddr, routingEntry.getnextHop(), routingEntry.getoutInterface());
            }
        }
        return msg;
    }

    private String getRoutingDecision(final String randomDestAddr,
                                      final Map<String, Routing> routingTableMap) {

        if (randomDestAddr.equals(Constants.LOOPBACK_ADDR)) {
            return String.format("%s is loopback; discarded.", Constants.LOOPBACK_ADDR);
        }


        for (String maskingAddr : Constants.MASKING_ADDR) {
            if (randomDestAddr.equals(maskingAddr)) {
                return String.format("%s is malformed; discarded.", maskingAddr);
            }
        }

        for (int i = 0; i < 4; ++i) {
            switch (i) {
                case 0: {
                    final String msg = getRoutingmsg(randomDestAddr, randomDestAddr, routingTableMap);
                    if (!msg.isEmpty()) {
                        return msg;
                    }
                    break;
                }
                case 1: {

                    String[] addressParts = randomDestAddr.split("\\.");
                    final String newAddress = addressParts[0] + "." + addressParts[1] + "." + addressParts[2] + "." + "0";
                    final String msg = getRoutingmsg(newAddress, randomDestAddr, routingTableMap);
                    if (!msg.isEmpty()) {
                        return msg;
                    }
                    break;
                }
                case 2: {

                    String[] addressParts = randomDestAddr.split("\\.");
                    final String newAddress = addressParts[0] + "." + addressParts[1] + "." + "0" + "." + "0";
                    final String msg = getRoutingmsg(newAddress, randomDestAddr, routingTableMap);
                    if (!msg.isEmpty()) {
                        return msg;
                    }
                    break;
                }
                case 3: {
                    String[] addressParts = randomDestAddr.split("\\.");
                    final String newAddress = addressParts[0] + "." + "0" + "." + "0" + "." + "0";
                    final String msg = getRoutingmsg(newAddress, randomDestAddr, routingTableMap);
                    if (!msg.isEmpty()) {
                        return msg;
                    }
                    break;
                }
            }
        }

        final Routing routingEntry = routingTableMap.get(Constants.DEFAULT_ADDR);
        return String.format("%s will be forwarded to %s out on outInterface %s.",
                randomDestAddr, routingEntry.getnextHop(), routingEntry.getoutInterface());
    }


    private Map<String, Routing> routingTableMemory() {
        final Map<String, Routing> routingTableMap = new LinkedHashMap<>();
        try (final FileReader rtFileReader = new FileReader(Constants.ROUTING_TABLE);
             final BufferedReader rtBufferReader = new BufferedReader(rtFileReader)) {
        	constructRoutingTable(routingTableMap, rtBufferReader);
            displayRoutingTableMemory(routingTableMap);
        } catch (final IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return routingTableMap;
    }

	private void constructRoutingTable(final Map<String, Routing> routingTableMap, final BufferedReader rtBufferReader)
			throws IOException {
		String destAddr;
		int masked;
		String nextHop;
		String outInterface;
		while ((destAddr = rtBufferReader.readLine()) != null) {
		    masked = Integer.parseInt(destAddr.split("/")[1]);
		    nextHop = rtBufferReader.readLine();
		    outInterface = rtBufferReader.readLine();
		    routingTableMap.put(destAddr.split("/")[0],
		            new Routing(destAddr.split("/")[0],
		                    masked,
		                    nextHop,
		                    outInterface));
		}
	}

	private void displayRoutingTableMemory(final Map<String, Routing> routingTableMap) {
		System.out.println("<========= Routing Table Memory =========>");
		System.out.printf("%-20s | %-12s | %-20s | %-20s%n",
		        "Destination Address",
		        "Ones In Mask",
		        "Next Hop Address",
		        "Outgoing outInterface");
		for (Map.Entry<String, Routing> entry : routingTableMap.entrySet()) {
		    System.out.printf("%-20s | %-12s | %-20s | %-20s%n",
		            entry.getKey(),
		            entry.getValue().getmasked(),
		            entry.getValue().getnextHop(),
		            entry.getValue().getoutInterface());
		}
	}
	
    public void execute() {
        final Map<String, Routing> routingTableMap = routingTableMemory();
        try (final FileReader randomPacketsFileReader = new FileReader(Constants.RANDOM_PACKET);
            final BufferedReader randomPacketsBufferedReader = new BufferedReader(randomPacketsFileReader)) {
            System.out.println("\n<========= Output - Routing Decisions =========>");
            analyzeRandomPackets(routingTableMap, randomPacketsBufferedReader);
        } catch (final IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

	private void analyzeRandomPackets(final Map<String, Routing> routingTableMap,
			final BufferedReader randomPacketsBufferedReader) throws IOException {
		String randomDestAddr;
		while ((randomDestAddr = randomPacketsBufferedReader.readLine()) != null) {
		    final String routingDecision = getRoutingDecision(randomDestAddr, routingTableMap);
		    System.out.println(routingDecision);
		    routingDecisionOutput(routingDecision);
		}
	}
}