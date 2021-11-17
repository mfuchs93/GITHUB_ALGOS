import java.util.Objects;

public class Vertex {
    private int id;
    private String name;
    private int dfsIndex;
    private int dfsLowLink;
    private boolean forbidden;

    public Vertex(int id, String name) {
        this.id = id;
        this.name = name;
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
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return getId() == vertex.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
