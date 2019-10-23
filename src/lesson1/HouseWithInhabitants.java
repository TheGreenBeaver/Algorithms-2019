package lesson1;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

class HouseWithInhabitants implements Comparable<HouseWithInhabitants>{
    private Address address;
    private ArrayList<String> inhabitants;

    HouseWithInhabitants(Address address, ArrayList<String> inhabitants) {
        this.address = address;
        this.inhabitants = inhabitants;
    }

    Address getAddress() {
        return address;
    }

    ArrayList<String> getInhabitants() {
        return inhabitants;
    }

    @Override
    public int compareTo(@NotNull HouseWithInhabitants o) {
        return address.compareTo(o.address);
    }
}
