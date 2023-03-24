import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


public class Reader {
    public ArrayList<String> reader(File file){
        ArrayList<String>text = new ArrayList<>();

            try {
                InputStreamReader r = new InputStreamReader(new FileInputStream(file));
                String str = r.getEncoding();
                if(!str.equals("UTF-8"))
                    str="ISO-8859-1";

                text = (ArrayList<String>) Files.readAllLines(Path.of(file.getPath()), Charset.forName(str));
            } catch (IOException e) {
                    e.printStackTrace();
            }

        return text;
    }
}
