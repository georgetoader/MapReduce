import java.util.List;
import java.util.Map;

// rezultatele etapei de Map
public class MapResult {
    private final String fileName;
    private final Map<Integer, Integer> dict;
    private final List<String> wordList;

    public MapResult(String fileName, Map<Integer, Integer> dict, List<String> wordList) {
        this.fileName = fileName;
        this.dict = dict;
        this.wordList = wordList;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<Integer, Integer> getDict() {
        return dict;
    }

    public List<String> getWordList() {
        return wordList;
    }
}
