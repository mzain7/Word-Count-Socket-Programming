import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public final class Main {

    public static void main(String[] args) throws Exception{
        Path path = Paths.get("src/dataset");
        File[] list = path.toFile().listFiles();
        assert list != null;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the word: ");
        String word= sc.nextLine();

        System.out.println("Total No. of \""+word+"\" words: "+countWithoutThread(list,word)+"\n");
        System.out.println("Total No. of \""+word+"\" words: "+countWithMultipleThread(list,word)+"\n");
        System.out.println("Total No. of \""+word+"\" words: "+countUsingServer(list,word)+"\n");



    }

    public static int countWithoutThread(File[] list,String word){
        long startTime = System.currentTimeMillis();
        WordCount count = new WordCount(list,word);
        count.reader();

        long endTime = System.currentTimeMillis();

        printLocation(count.result);
        System.out.println("\nTime Taken Without Thread = "+(endTime-startTime)+"ms");
        System.out.println("Total No. of words: " + count.totalCount);
        return count.count;
    }

    public static int countWithMultipleThread (File[] list,String word){
        long startTime = System.currentTimeMillis();

        int size;
        if(list.length%10 == 0){
            size = list.length/10;
        }else {
            size = list.length/10 + 1;
        }
        WordCount[] wordCount = new WordCount[size];

        for (int i = 0; i < size; i++) {
            if(i != size-1){
                wordCount[i] = new WordCount(Arrays.copyOfRange(list, (int) i * (list.length / size), (int) (i + 1) * (list.length / size)),word);
            }else {
                wordCount[i] = new WordCount(Arrays.copyOfRange(list,(int)i*(list.length/size),list.length),word);

            }
            wordCount[i].start();
        }
        try {
            for (int i = 0; i < wordCount.length; i++) {
            wordCount[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.currentTimeMillis();


        ArrayList<String> result = new ArrayList<>();
        int totalCount=0;
        int counter=0;
        for (int i = 0; i < wordCount.length; i++) {
            counter += wordCount[i].count;
            result.addAll(wordCount[i].result);
            totalCount += wordCount[i].totalCount;

        }
        printLocation(result);
        System.out.println("\nTime Taken Using Thread = "+(endTime-startTime)+"ms");
        System.out.println("Total No. of words: " + totalCount);

        return counter;
    }

    public static int countUsingServer(File[] list,String word){
        long startTime = System.currentTimeMillis();

        int count=0;
        int totalCount=0;
        ArrayList<String> result;
        try {
            System.out.println("Client Started");
            Socket socket = new Socket("localhost",8080);
            socket.setKeepAlive(true);

            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
            writer.println(word);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            File[] newList = Arrays.copyOfRange(list,0,100);

            out.writeObject(newList);
            for (File file: newList) {
                Reader reader = new Reader();
                out.writeObject(reader.reader(file));
            }

            BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            result = (ArrayList<String>)input.readObject();

            totalCount=Integer.parseInt(read.readLine());
            count=Integer.parseInt(read.readLine());

            long endTime = System.currentTimeMillis();
            printLocation(result);

            System.out.println("\nTime Taken Using Server = "+(endTime-startTime)+"ms");
            System.out.println("Total No. of words: " + totalCount);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server Not Running!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static void printLocation(ArrayList<String> result){
        for (String str : result) {
            System.out.println(str);
        }
    }
}
