

class Validator {
    public static boolean isPositiveDouble(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            return Double.parseDouble(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidAccountNumber(String accNum) {
        return accNum != null && !accNum.trim().isEmpty();
    }
}