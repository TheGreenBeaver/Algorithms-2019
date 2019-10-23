package lesson1;

import org.jetbrains.annotations.NotNull;

public class Address implements Comparable<Address> {
    private String street;
    private int house;

    Address(String street, int house) {
        this.street = street;
        this.house = house;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Address other = (Address)obj;
        return (house == other.house && street.equals(other.street));
    }

    @Override
    public int compareTo(@NotNull Address o) {
        if (street.equals(o.street)) {
            return house - o.house;
        }

        return street.compareTo(o.street);
    }

    @Override
    public String toString() {
        return street + " " + house;
    }
}