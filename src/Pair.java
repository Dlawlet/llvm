import java.util.Objects;

/* Inspired from
 * https://stackoverflow.com/questions/24433184/overriding-hashcode-with-a-class-with-two-generics-fields
 * Java does not provide a Pair class by default...
 */
public class Pair<L,R> {
    private L left;
    private R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
    public void setLeft(L left) {
        this.left = left;
    }


    public void setRight(R right) {
        this.right = right;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair< ? , ? >)
            {
                Pair< ? , ? > pair = (Pair< ? , ? >)obj;
                return left.equals(pair.getLeft()) && right.equals(pair.getRight());
            }
        return false;
    }

    @Override
    public String toString() {
        return "Pair " + Integer.toHexString(hashCode()) + ": (" + left.toString() + ", " + right.toString()
            + ")";
    }

    public int hashCode() {
        return Objects.hash(left,right);
    }
}
