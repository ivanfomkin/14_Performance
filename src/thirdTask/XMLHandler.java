package secondTask;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XMLHandler extends DefaultHandler {
    private Voter voter;
    private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    //Поменяем тип значения в Map с Integer на Byte, вряд ли один избиратель будет голосовать более 127 раз...
    //Так же нет смысла хранить целый объект Voter как ключ, легковеснее будет хранить Voter, приведённый к String
    //Чтобы сразу его потом печатать
//    private Map<Voter, Integer> voterCounts; - было так
    private Map<String, Byte> voterCounts;

    public XMLHandler() {
        voterCounts = new HashMap<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try {
            if (qName.equals("voter") && voter == null) {
                Date birthDay = birthDayFormat.parse(attributes.getValue("birthDay"));
                voter = new Voter(attributes.getValue("name"), birthDay);
            } else if (qName.equals("visit") && voter != null) {
                //Ниже вставляем в Map уже объекты типа String и Byte
                byte counts = voterCounts.getOrDefault(voter.toString(), (byte) 0); //counts тоже byte
                voterCounts.put(voter.toString(), (byte) (counts + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("voter")) {
            voter = null;
        }
    }

    public void printDuplicatedVoters() {
        //Соответственно тут пробегаемся уже по объектам String, а не Voter
        for (String voter : voterCounts.keySet()) {
            //count тоже изменим с int на byte
            byte count = voterCounts.get(voter);
            if (count > 1) {
                System.out.println(voter + " - " + count);
            }
        }
    }
}
