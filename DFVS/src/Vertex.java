import java.util.Objects;

public class Vertex {
    private int id;
    private String name;
    private int dfsIndex;
    private int dfsLowLink;
    private boolean forbidden;
    private char polarity;

    public int getCycleCount() {
        return cycleCount;
    }

    public void setCycleCount(int cycleCount) {
        this.cycleCount = cycleCount;
    }

    private int cycleCount;

    private int maxPetal;
    private int petal;

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    private Vertex parent;

    public int getMaxPetal() {
        return maxPetal;
    }

    public void setMaxPetal(int maxPetal) {
        this.maxPetal = maxPetal;
    }

    public int getPetal() {
        return petal;
    }

    public void setPetal(int petal) {
        this.petal = petal;
    }

    public char getPolarity() {
        return polarity;
    }

    public void setPolarity(char polarity) {
        this.polarity = polarity;
    }

    public Vertex(int id, String name) {
        this.id = id;
        this.name = name;
        this.polarity = '.';
    }

    public boolean isForbidden() {
        return forbidden;
    }

    public void setForbidden(boolean forbidden) {
        this.forbidden = forbidden;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDfsIndex() {
        return dfsIndex;
    }

    public void setDfsIndex(int dfsIndex) {
        this.dfsIndex = dfsIndex;
    }

    public int getDfsLowLink() {
        return dfsLowLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDfsLowLink(int dfsLowLink) {
        this.dfsLowLink = dfsLowLink;
    }

    @Override
    public String toString() {
        return name + polarity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return getId() == vertex.getId() && getPolarity() == vertex.getPolarity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPolarity());
    }
}
