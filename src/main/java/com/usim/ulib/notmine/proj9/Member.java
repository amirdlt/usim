package com.usim.ulib.notmine.proj9;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;

public class Member {
    protected static final int MAX_NUM_OF_ITEMS = 250;

    protected final String name;
    protected final MemberType type;
    protected final Item[] borrowedItems;
    protected final LocalDate[] borrowingTimes;
    protected final LocalDate[] returningTimes;
    protected int numOfBorrowedItems;

    protected Member(String name, MemberType type) {
        this.name = name;
        this.type = type;
        borrowedItems = new Item[MAX_NUM_OF_ITEMS];
        borrowingTimes = new LocalDate[MAX_NUM_OF_ITEMS];
        returningTimes = new LocalDate[MAX_NUM_OF_ITEMS];
        numOfBorrowedItems = 0;
    }

    public void borrowItem(Item item, String date) {
        if (numOfNotReturnedItems() > (type == MemberType.STUDENT_BACHELOR ? 3 : type == MemberType.STUDENT_SENIOR ? 5 : 10) || item.isBorrowed())
            return;
        borrowedItems[numOfBorrowedItems] = item;
        int[] d = Arrays.stream(date.split("-")).mapToInt(Integer::parseInt).toArray();
        borrowingTimes[numOfBorrowedItems++] = LocalDate.of(d[0], d[1], d[2]);
    }

    public void returnItem(Item item, String date) {
        item.setBorrowed(false);
        for (int i = 0; i < numOfBorrowedItems; i++)
            if (borrowedItems[i].equals(item)) {
                int[] d = Arrays.stream(date.split("-")).mapToInt(Integer::parseInt).toArray();
                returningTimes[i] = LocalDate.of(d[0], d[1], d[2]);
                return;
            }
    }

    public int numOfNotReturnedItems() {
        int count = 0;
        for (int i = 0; i < numOfBorrowedItems; i++)
            if (borrowingTimes[i] == null)
                count++;
        return count;
    }

    public int penalty(String date) {
        int res = 0;
        int[] d = Arrays.stream(date.split("-")).mapToInt(Integer::parseInt).toArray();
        LocalDate now = LocalDate.of(d[0], d[1], d[2]);
        int unreturned = numOfNotReturnedItems();
        for (int i = 0; i < numOfBorrowedItems; i++) {
            if (returningTimes[i] != null)
                continue;
            long days = Period.between(borrowingTimes[i], now).getDays() -
                    (type == MemberType.TEACHER ? (unreturned < 5 ? 20 : 10) : 10);
            if (days < 0)
                continue;
            if (borrowedItems[i] instanceof Book) {
                res += days < 8 ? days * 100 : 700 + (days - 7) * 200;
            } else if (borrowedItems[i] instanceof DVD)  {
                res += days * 200;
            }
        }
        return res;
    }

    public enum MemberType {
        TEACHER,
        STUDENT_BACHELOR,
        STUDENT_SENIOR
    }
}
