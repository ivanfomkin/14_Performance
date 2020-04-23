package thirdTask;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

import static thirdTask.XMLHandler.voters;

public class Loader {
    //Сократим класс Loader до нескольких строчек кода. Для вставки в БД нам будет достаточно следующего кода:
    public static void main(String[] args) throws Exception {

        String fileName = "res/data-1572M.xml";
        long start = System.currentTimeMillis();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLHandler handler = new XMLHandler();
        parser.parse(new File(fileName), handler);
        voters.forEach((n, b) -> {
            try {
                DBConnection.countVoter(n, b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        DBConnection.executeMultiInsert(); //Выполним insert того, что осталось в StringBuilder
        System.out.println("Time: " + (System.currentTimeMillis() - start) / 1000 / 60 + " minutes");
    }
}