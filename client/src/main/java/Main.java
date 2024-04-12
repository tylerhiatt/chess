import ui.Client;

public class Main {

    private static final Client client = new Client();

    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        // interact with server with client UI
        int port = 8080;
        client.clientStart(port);
    }


}