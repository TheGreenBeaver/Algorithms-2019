package lesson1;

public class Address {
    String street;
    int house;

    Address(String street, int house) {
        this.street = street;
        this.house = house;
    }

    boolean isAlphabeticallyEarlier(Address other) {
        String firstStreet = Util.alphabeticallyFirstString(this.street, other.street, 0);
        return (firstStreet == null && this.house < other.house || firstStreet != null && firstStreet.equals(this.street));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Address other = (Address)obj;
        return (this.house == other.house && this.street.equals(other.street));
    }
}