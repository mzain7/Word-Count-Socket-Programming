import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class Server {
    public static void main(String[] args) {
        int count = 0;
        int totalCount = 0;
        ArrayList<String> result = new ArrayList<>();
        try {
            System.out.println("Waiting for Client");
            ServerSocket serverSocket = new ServerSocket(60);
            Socket socket = serverSocket.accept();
            socket.setKeepAlive(true);
            System.out.println("Connection Established");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String word = reader.readLine();

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            File[] files = (File[]) input.readObject();


            for (int i = 0; i < files.length; i++) {
                ArrayList<String> text = (ArrayList<String>) input.readObject();
                for (int j = 0; j < text.size(); j++) {

                    String[] words = text.get(j).split("\\s+");
                    totalCount += words.length;

                    for (int k = 0; k < words.length; k++) {
                        if (words[k].equals(word)) {
                            count++;
                            result.add(
                                    String.format("File Name: %s || Line# %d || Word# %d", files[i].getName(), j+1, k+1));
                        }

                    }
                }

            }

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(result);

            writer.println(totalCount);
            writer.println(count);

            socket.close();
            serverSocket.close();
            System.out.println("Socket Closed");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}