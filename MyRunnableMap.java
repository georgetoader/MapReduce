import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class MyRunnableMap implements Runnable {
    private final String fileName;
    private final int offset;
    private final int dim;
    private final List<MapResult> mapResults;

    public MyRunnableMap(String fileName, int offset, int dim,
                        List<MapResult> mapResults) {
        this.fileName = fileName;
        this.offset = offset;
        this.dim = dim;
        this.mapResults = mapResults;
    }

    @Override
    public void run() {
        byte[] sequence = new byte[dim];

        try (RandomAccessFile file = new RandomAccessFile(fileName, "r")) {
            // folosesc caracterul anterior pentru a verifica inceperea in mijlocul cuvantului
            char charBefore = ' ';
            if (offset > 0) {
                file.seek(offset - 1);
                charBefore = (char)file.read();
            }

            file.seek(offset);
            file.readFully(sequence);
            StringBuilder fragment = new StringBuilder(new String(sequence));
            // elimin partea de cuvant de la inceput daca exista
            if (Character.isLetter(charBefore) || Character.isDigit(charBefore)) {
                int pos = 0;
                while (Character.isLetter(fragment.charAt(pos)) || 
                        Character.isDigit(fragment.charAt(pos))) {
                    pos++;
                }
                fragment.delete(0, pos);
            }

            // elimin caracterele non-litera si non-digit de la inceput
            while (!Character.isLetter(fragment.charAt(0)) && 
                    !Character.isDigit(fragment.charAt(0))) {
                fragment.deleteCharAt(0);
            }

            // completez cuvantul taiat de la final
            char chr = fragment.charAt(fragment.length() - 1);
            int pos = offset + dim - 1;
            while (Character.isLetter(chr) || Character.isDigit(chr)) {
                if (pos > offset + dim - 1) {
                    fragment.append(chr);
                }
                pos++;
                file.seek(pos);
                chr = (char)file.read();
            }

            // elimin caracterele non-litera si non-digit de la sfarsit
            while (!Character.isLetter(fragment.charAt(fragment.length() - 1)) && 
                    !Character.isDigit(fragment.charAt(fragment.length() - 1))) {
                fragment.deleteCharAt(fragment.length() - 1);
            }

            // impart fragmentul in cuvinte
            String[] words = fragment.toString().split("[\\W_]+");
            Map<Integer, Integer> dict = new HashMap<>();
            int maxLength = 0;
            // construiesc dictionarul cu numarul de aparitii pentru lungimile cuvintelor
            for (String str : words) {
                int size = str.length();
                if (size > maxLength) {
                    maxLength = size;
                }
                int val = dict.get(size) == null ? 0 : dict.get(size);
                dict.put(size, val + 1);
            }
            // construiesc lista cuvintelor de lungime maxima
            List<String> maxWords = new ArrayList<>();
            for (String str : words) {
                if (str.length() == maxLength) {
                    maxWords.add(str);
                }
            }

            // rezultatele etapei de Map
            synchronized (mapResults) {
                mapResults.add(new MapResult(fileName, dict, maxWords));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
