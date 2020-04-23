package thirdTask;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Немнго оптимизируем класс XMLHandler, теперь он только вызывает метод countVoter в классе
 * DBConnection. Это сделано для простоты и снижения времени работы программы.
 */
public class XMLHandler extends DefaultHandler {
    protected static Map<String, String> voters = new HashMap<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try {
            if (qName.equals("voter")) {
                voters.put(attributes.getValue("name"), attributes.getValue("birthDay"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
