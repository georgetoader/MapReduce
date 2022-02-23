import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Tema2 {
    
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        int P = Integer.parseInt(args[0]);
        String inFile = args[1];
        String outFile = args[2];

        ExecutorService tpeMap = Executors.newFixedThreadPool(P);
        ExecutorService tpeReduce = Executors.newFixedThreadPool(P);
        List<MapResult> mapResults = new ArrayList<>();
        List<ReduceResult> reduceResults = new ArrayList<>();

        int dim = 0, nrFiles = 0;
        ArrayList<String> files = new ArrayList<>();
        // citesc datele din fisierul de input
        try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
            dim = Integer.parseInt(br.readLine());
            nrFiles = Integer.parseInt(br.readLine());
            String str;
            while ((str = br.readLine()) != null) {
                files.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // operatiile de tip Map
        // iau fiecare fisier si il separ in secvente de cate "dim" bytes
        for (int i = 0; i < nrFiles; i++) {
            File newFile = new File(files.get(i));
            long fileSize = newFile.length();
            int pos = 0;
            // impart fisierul in taskuri de dimensiune "dim"
            while (pos + dim < fileSize) {
                tpeMap.submit(new MyRunnableMap(files.get(i), pos, dim, mapResults));
                pos += dim;
            }
            // inca un task pentru caracterele ramase
            if (pos < fileSize) {
                int newDim = (int)fileSize - pos;
                tpeMap.submit(new MyRunnableMap(files.get(i), pos, newDim, mapResults));
            }
        }

        tpeMap.shutdown();
        try {
            tpeMap.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // operatiile de tip Reduce
        // pentru fiecare fisier creez noul task de tip Reduce
        for (int i = 0; i < nrFiles; i++) {
            List<MapResult> list = new ArrayList<>();
            // grupez rezultatele in functie de fisierul folosit
            for (MapResult mp : mapResults) {
                if (mp.getFileName().equals(files.get(i))) {
                    list.add(mp);
                }
            }
            tpeReduce.submit(new MyRunnableReduce(files.get(i), list, reduceResults));
        }

        tpeReduce.shutdown();
        try {
            tpeReduce.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // sortez rezultatele din urma etapei de Reduce si le scriu in fisierul de output
        reduceResults.sort(Comparator.comparing(ReduceResult::getDocRank, Comparator.reverseOrder())
                                    .thenComparing(ReduceResult::getFileName));
        try (BufferedWriter out = new BufferedWriter(new FileWriter(outFile))) {
            for (ReduceResult res : reduceResults) {
                String resToString = res.toString() + "\n";
                out.write(resToString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}