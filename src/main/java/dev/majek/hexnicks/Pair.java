package dev.majek.hexnicks;

import java.util.Objects;

/**
 * Stores two related values. This data structure is very useful if an object must
 * be passed with a property that is not part of that object. This class is based
 * off of the C++ struct <code>std::pair</code>.
 *
 * @param <A> the first type.
 * @param <B> the second type.
 */
public final class Pair<A, B> {
    // the first value
    private A first;
    // the second value
    private B second;

    /**
     * Constructs a new instance of <code>Pair</code> with the two values stored
     * in the pair.
     *
     * @param first the first value.
     * @param second the second value.
     */
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Constructs a new instance of <code>Pair</code> without specifying the two
     * values stored in the pair.
     */
    public Pair() {
        this(null, null);
    }

    /**
     * Get the first value in the pair.
     *
     * @return the first value in the pair.
     */
    public A getFirst() {
        return first;
    }

    /**
     * Set the first value in the pair to the specified value.
     *
     * @param newValue the new first value.
     */
    public void setFirst(A newValue) {
        first = newValue;
    }

    /**
     * Get the second value in the pair.
     *
     * @return the second value in the pair.
     */
    public B getSecond() {
        return second;
    }

    /**
     * Set the second value in the pair to the specified value.
     *
     * @param newValue the new second value.
     */
    public void setSecond(B newValue) {
        second = newValue;
    }

    /**
     * Creates a hash code for this object.
     *
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(first);
        hash = 13 * hash + Objects.hashCode(second);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object object) {
        if(this == object)
            return true;
        if(object == null || !Pair.class.equals(object.getClass()))
            return false;
        Pair pair = (Pair)object;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return '{' + Objects.toString(first) + ", " + Objects.toString(second) + '}';
    }
}

