package secondTask;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Memory usage on start: " + memoryUsage/1024 + " kb");
        String fileName = "res/data-18M.xml";

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLHandler handler = new XMLHandler();
        parser.parse(new File(fileName), handler);
        handler.printDuplicatedVoters();

        memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memoryUsage;
        System.out.println("Memory usage with SAX parser: " + memoryUsage/1024 + " kb");
        memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        parseFile(fileName);

//        Printing results
        System.out.println("Voting station work times: ");
        //ниже тоже поменяем Integer на short у итератора
        for (short votingStation : voteStationWorkTimes.keySet()) {
            WorkTime workTime = voteStationWorkTimes.get(votingStation);
            System.out.println("\t" + votingStation + " - " + workTime);
        }

        System.out.println("Duplicated voters: ");
        for (String voter : voterCounts.keySet()) { //Тут будем перебирать String, а не Voter
            //Тут тип был Integer, переделаем на byte
            byte count = voterCounts.get(voter);
            if (count > 1) {
                System.out.println("\t" + voter + " - " + count);
            }
        }
        memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memoryUsage;
        System.out.println("Memory usage with DOM parser: " + memoryUsage/1024 + " kb");
    }

    private static void parseFile(String fileName) throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(fileName));

        findEqualVoters(doc);
        fixWorkTimes(doc);
    }

    private static void findEqualVoters(Document doc) throws Exception {
        NodeList voters = doc.getElementsByTagName("voter");
        int votersCount = voters.getLength();
        for (int i = 0; i < votersCount; i++) {
            Node node = voters.item(i);
            NamedNodeMap attributes = node.getAttributes();

            String name = attributes.getNamedItem("name").getNodeValue();
            Date birthDay = birthDayFormat.parse(attributes.getNamedItem("birthDay").getNodeValue());

            Voter voter = new Voter(name, birthDay);
            //Тут count тоже был Integer, тоже переделаем на byte и съэкономим память
//            Integer count = voterCounts.get(voter); - было так
            byte count = voterCounts.getOrDefault(voter.toString(), (byte) 0);
            voterCounts.put(voter.toString(), (byte) (count == 0  ? 1 : count + 1));
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