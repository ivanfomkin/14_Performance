package thirdTask;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Немнго оптимизируем класс XMLHandler, теперь он только вызывает метод countVoter в классе
 * DBConnection. Это сделано для простоты и снижения времени работы программы.
 */
public class XMLHandler extends DefaultHandler {

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try {
            if (qName.equals("voter")) {
                DBConnection.countVoter(attributes.getValue("name"), attributes.getValue("birthDay"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
