
public class Main {
    public static void main(String[] args) throws Exception {
        int port = 8002;
        if (args.length == 1){
            port = Integer.parseInt(args[0]);
        }
        System.out.printf("Listening on port: %s...\n", port);
        new Server(port);

    }
}