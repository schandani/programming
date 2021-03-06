package ch2;

import util.Pair;
import util.SingleLLNode;

import java.util.Arrays;

/**
 * Ethan Petuchowski 3/2/16
 *
 * Bottom-Up Linked-List Merge-Sort
 *
 * This was way more confusing than I expected. But I think it works properly. Since I don't know
 * the "real" algorithm I'm not sure if this is the best way to implement it. All I know is it works
 * on whatever inputs I've tried it on and it's a cool algorithm.
 */
@SuppressWarnings("Duplicates")
public class BULLMerge<T extends Comparable<T>> {
    private SingleLLNode<T> head = null;

    public SingleLLNode<T> getHead() {
        return head;
    }

    public void setHead(SingleLLNode<T> head) {
        this.head = head;
    }

    public static void main(String[] args) {
        char[] testChars = "MergeSortExample".toLowerCase().toCharArray();
        SingleLLNode<Character> cur = null;
        BULLMerge<Character> merger = new BULLMerge<>();
        for (char c : testChars) {
            if (cur == null) {
                merger.setHead(new SingleLLNode<>(c));
                cur = merger.getHead();
            }
            else {
                cur.nxt = new SingleLLNode<>(c);
                cur = cur.nxt;
            }
        }
        assert merger.getHead() != null;
        System.out.println(merger.getHead().listString());
        merger.sort(testChars.length);
        Arrays.sort(testChars);
        String truth = new String(testChars);
        System.out.println(truth);
        String actuality = merger.getHead().listString();
        System.out.println(actuality);
        assert truth.equals(actuality);
    }

    /**
     * @param a    first element of first list
     * @param aLen length of first list
     * @param bLen length of second list
     * @return the beginning and the last node in the list. However the elements of the list come
     * back rearranged such that it and the "second" list after it (see below) are "merged", as known
     * from the classic mergesort algorithm.
     */
    private Pair<SingleLLNode<T>> merge(SingleLLNode<T> a, int aLen, int bLen) {
        if (bLen == 0 || a == null) return new Pair<>(a, a);

        SingleLLNode<T> ret = null;
        SingleLLNode<T> cur = null;

        SingleLLNode<T> b = a;

        // "alreadySorted" is an optimization
        boolean alreadySorted = false;
        for (int skip = 0; skip++ < aLen; )
            if (skip == aLen && b.val.compareTo(b.nxt.val) < 0) alreadySorted = true;
            else b = b.nxt;

        if (alreadySorted) {
            for (int skip = 0; skip++ < bLen; )
                b = b.nxt;
            return new Pair<>(a, b);
        }

        for (int i = 0, j = 0, k = 0; i < aLen+bLen; i++) {
            if (j >= aLen) {
                assert cur != null;
                cur.nxt = new SingleLLNode<>(b.val);
                b = b.nxt;
                cur = cur.nxt;
                k++;
            }
            else if (k >= bLen) {
                assert cur != null;
                cur.nxt = new SingleLLNode<>(a.val);
                a = a.nxt;
                cur = cur.nxt;
                j++;
            }
            else if (b.val.compareTo(a.val) < 0) {
                if (ret == null) {
                    ret = new SingleLLNode<>(b.val);
                    b = b.nxt;
                    cur = ret;
                    k++;
                }
                else {
                    cur.nxt = new SingleLLNode<>(b.val);
                    b = b.nxt;
                    cur = cur.nxt;
                    k++;
                }
            }
            else {
                if (ret == null) {
                    ret = new SingleLLNode<>(a.val);
                    a = a.nxt;
                    cur = ret;
                    j++;
                }
                else {
                    cur.nxt = new SingleLLNode<>(a.val);
                    a = a.nxt;
                    cur = cur.nxt;
                    j++;
                }
            }
        }
        assert cur != null;
        cur.nxt = b;
        return new Pair<>(ret, cur);
    }

    public SingleLLNode<T> sort(int size) {
        for (int sz = 1; sz <= size; sz *= 2) {
            SingleLLNode<T> front = head;
            SingleLLNode<T> last = null;
            Pair<SingleLLNode<T>> pair;
            for (int frontIdx = 0; frontIdx <= size-sz; frontIdx += Math.min(2*sz, size-frontIdx)) {
                pair = merge(front, sz, Math.min(sz, size-(frontIdx+sz)));
                if (last == null) head = pair.first;
                else last.nxt = pair.first;
                last = pair.second;
                front = last.nxt;
            }
        }
        return head;
    }
}
