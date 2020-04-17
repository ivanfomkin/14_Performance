package firstTask;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Generator implements Runnable {
    private PrintWriter writer;
    private int startRegion;
    private int stopRegion;

    public Generator(int startRegion, int stopRegion, int threadNumber) throws FileNotFoundException {
        StringBuilder name = new StringBuilder().append("res/numbers")
                .append(threadNumber).append(".txt");
        this.writer = new PrintWriter(name.toString());
        this.startRegion = startRegion;
        this.stopRegion = stopRegion;
    }

    @Override
    public void run() {
        char letters[] = {'У', 'К', 'Е', 'Н', 'Х', 'В', 'А', 'Р', 'О', 'С', 'М', 'Т'};

        for (int regionCode = startRegion; regionCode < stopRegion && regionCode < 100; regionCode++) {
            StringBuilder builder = new StringBuilder();
            for (int number = 1; number < 1000; number++) {
                for (char firstLetter : letters) {
                    for (char secondLetter : letters) {
                        for (char thirdLetter : letters) {
                            builder.append(firstLetter).append(padNumber(number, 3))
                                    .append(secondLetter).append(thirdLetter)
                                    .append(padNumber(regionCode, 2))
                                    .append("\n");
                        }
                    }
                }
            }
            writer.write(builder.toString());
        }
        writer.flush();
        writer.close();
    }

    private static String padNumber(int number, int numberLength) {
        StringBuilder numberStr = new StringBuilder().append(number);
        int padSize = numberLength - numberStr.length();
        for (int i = 0; i < padSize; i++) {
            numberStr.insert(0, '0');
        }
        return numberStr.toString();
    }
}