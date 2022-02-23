import java.util.*;

public class MyRunnableReduce implements Runnable {
    private final String fileName;
    private final List<MapResult> mapResults;
    private final List<ReduceResult> reduceResults;

    private final Map<Integer, Integer> dict = new HashMap<>();
    private final List<String> maxWords = new ArrayList<>();

    public MyRunnableReduce(String fileName, List<MapResult> mapResults,
                            List<ReduceResult> reduceResults) {
        this.fileName = fileName;
        this.mapResults = mapResults;
        this.reduceResults = reduceResults;
    }

    @Override
    public void run() {
        // etapa de combinare
        for (MapResult res : mapResults) {
            Map<Integer, Integer> resDict = res.getDict();
            List<String> resList = res.getWordList();

            resDict.forEach((key, value) -> dict.merge(key, value, Integer::sum));
            // lista cuvintelor de lungime maxima
            if (maxWords.size() == 0 || maxWords.get(0).length() < resList.get(0).length()) {
                maxWords.clear();
                maxWords.addAll(resList);
            } else if (maxWords.get(0).length() == resList.get(0).length()) {
                maxWords.addAll(resList);
            }
        }

        // etapa de procesare
        double totalWords = 0, sum = 0;
        for (Integer wordLen : dict.keySet()) {
            sum += getFibboRank(wordLen + 1) * dict.get(wordLen);
            totalWords += dict.get(wordLen);
        }
        double docRank = sum / totalWords;
        int maxLength = Collections.max(dict.keySet());
        String[] str = fileName.split("/");

        // rezultatele etapei de Reduce
        synchronized (reduceResults) {
            reduceResults.add(new ReduceResult(str[str.length - 1], docRank, maxLength, dict.get(maxLength)));
        }
    }

    private int getFibboRank(int n) {
        if (n <= 1) {
            return n;
        }
        return getFibboRank(n - 1) + getFibboRank(n - 2);
    }
}
