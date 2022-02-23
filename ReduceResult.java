// rezultatele etapei de Reduce
public class ReduceResult {
    private final String fileName;
    private final Double docRank;
    private final Integer maxLength;
    private final Integer maxNumber;

    public ReduceResult(String fileName, Double docRank, Integer maxLength, Integer maxNumber) {
        this.fileName = fileName;
        this.docRank = docRank;
        this.maxLength = maxLength;
        this.maxNumber = maxNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public Double getDocRank() {
        return docRank;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    @Override
    public String toString() {
        return fileName + "," + String.format("%.2f", docRank) + "," + maxLength + "," + maxNumber;
    }
}
