package rubiconproject.utils;

public interface DataProvider<T> {

    public T getNext() throws InterruptedException;
}
