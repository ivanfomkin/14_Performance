package thirdTask;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import secondTask.WorkTime;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Loader {
    private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static SimpleDateFormat visitDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    //Тут можно поменять Integer на Short для экономии памяти
    private static HashMap<Short, WorkTime> voteStationWorkTimes = new HashMap<>();
    //Поменяем тип значения в Map с Integer на Byte, вряд ли один избиратель будет голосовать более 127 раз...
    //Так же нет смысла хранить целый объект Voter как ключ, легковеснее будет хранить Voter, приведённый к String
    //Чтобы сразу его потом печатать, то же самое сделаем в XMLHandler
    private static HashMap<String, Byte> voterCounts = new HashMap<>();

    public static void main(String[] args) throws Exception {

        String fileName = "res/data-0.2M.xml";
//
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        SAXParser parser = factory.newSAXParser();
//        XMLHandler handler = new XMLHandler();
//        parser.parse(new File(fileName), handler);
//        handler.printDuplicatedVoters();
//
        long start = System.currentTimeMillis();

        parseFile(fileName);
        System.out.println("Time: " + (System.currentTimeMillis() - start) + " ms");
        DBConnection.printVoterCounts();
    }

    private static void parseFile(String fileName) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(fileName));

        findEqualVoters(doc);
//        fixWorkTimes(doc);
    }

    private static void findEqualVoters(Document doc) throws Exception {
        NodeList voters = doc.getElementsByTagName("voter");
        int votersCount = voters.getLength();
        for (int i = 0; i < votersCount; i++) {
            Node node = voters.item(i);
            NamedNodeMap attributes = node.getAttributes();

            String name = attributes.getNamedItem("name").getNodeValue();
//            Date birthDay = birthDayFormat.parse(attributes.getNamedItem("birthDay").getNodeValue());
            String birthDay = attributes.getNamedItem("birthDay").getNodeValue();
            DBConnection.countVoter(name, birthDay);
        }
    }

    private static void fixWorkTimes(Document doc) throws Exception {
        NodeList visits = doc.getElementsByTagName("visit");
        int visitCount = visits.getLength();
        for (int i = 0; i < visitCount; i++) {
            Node node = visits.item(i);
            NamedNodeMap attributes = node.getAttributes();

            //Тут Integer можно заменить на short, byte не подходит, т.к. есть номера станций с номером более 127
            short station = Short.parseShort(attributes.getNamedItem("station").getNodeValue());
            Date time = visitDateFormat.parse(attributes.getNamedItem("time").getNodeValue());
            WorkTime workTime = voteStationWorkTimes.get(station);
            if (workTime == null) {
                workTime = new WorkTime();
                voteStationWorkTimes.put(station, workTime);
            }
            workTime.addVisitTime(time.getTime());
        }
    }
}