import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WordCount extends Thread{
    File [] list;
    String word;
    int totalCount = 0;

    int count;

    ArrayList<String> result = new ArrayList<>();



    public WordCount(File[] list, String word){
        this.list = list;
        this.word = word;
    }
    public void reader(){

        for (File file : list) {
            try {
                InputStreamReader r = new InputStreamReader(new FileInputStream(file));
                String str = r.getEncoding();
                if(!str.equals("UTF-8"))
                    str="ISO-8859-1";
                List<String> lines = Files.readAllLines(Path.of(file.getPath()), Charset.forName(str));
                matcher(lines,file);

            } catch (IOException e) {

            }

        }
    }

    public void matcher(List<String> lines,File file){

        for (int i = 0; i < lines.size(); i++) {
            String[] arr = lines.get(i).split("\\s+");
            totalCount += arr.length;

            for (int j = 0; j < arr.length; j++) {
                if(arr[j].equals(word)){
                    result.add(String.format("File Name: %s || Line# %d || Word# %d",file.getName(),i+1,j+1));
                    count++;
                }
            }
        }
    }

    @Override
    public void run() {
        reader();
    }
}
