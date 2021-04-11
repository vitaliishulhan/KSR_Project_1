package extraction;

/**
 * Numerical representation of the countries for classification
 */
public enum Place {
    WEST_GERMANY(0),
    USA(1),
    FRANCE(2),
    UK(3),
    CANADA(4),
    JAPAN(5),
    UNDEFINED(-1);

    /**
     * Numerical value of country
     */
    private final int value;

    /**
     * Private constructor for numerical representation
     * @param value numerical representation of country
     */
    Place(int value) {
        this.value = value;
    }

    /**
     * value getter
     * @return numerical representation of country
     */
    public int getValue() {
        return value;
    }

    /**
     * gets place due to numerical value
     * @param value numerical representation of country
     * @return enum object representating country
     */
    public static Place getPlaceFromInt(int value) {
        return switch (value) {
            case 0 -> WEST_GERMANY;
            case 1 -> USA;
            case 2 -> FRANCE;
            case 3 -> UK;
            case 4 -> CANADA;
            case 5 -> JAPAN;
            default -> UNDEFINED;
        };
    }

    /**
     * gets place due to name (it's used while articles parsing)
     * @param place country name
     * @return enum object representating country
     */
    public static Place getPlaceFromString(String place) {
        return switch (place) {
            case "west-germany" -> WEST_GERMANY;
            case "usa" -> USA;
            case "france" -> FRANCE;
            case "uk" -> UK;
            case "canada" -> CANADA;
            case "japan" -> JAPAN;
            default -> UNDEFINED;
        };
    }

    /**
     * Returns country amount which is classifiable
     * @return country amount
     */
    public static int getPlacesAmount() {
        return Place.values().length;
    }

    /**
     * Returns country name
     * @return country name
     */
    @Override
    public String toString() {
        return switch (value) {
            case 0 -> "west-germany";
            case 1 -> "usa";
            case 2 -> "france";
            case 3 -> "uk";
            case 4 -> "canada";
            case 5 -> "japan";
            default -> "undefined";
        };
    }
}
