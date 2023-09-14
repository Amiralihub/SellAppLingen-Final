package sellapp.models;

import android.annotation.SuppressLint;
import android.widget.EditText;

public class ValidationManager {

    private static EditText editStoreName;
    private static EditText editOwner;
    private static EditText editStreet;
    private static EditText editHouseNumber;
    private static EditText editZip;
    private static EditText editTelephone;
    private static EditText editEmail;


    public ValidationManager(EditText editStoreName, EditText editOwner, EditText editStreet,
                             EditText editHouseNumber, EditText editZip, EditText editTelephone,
                             EditText editEmail) {
        this.editStoreName = editStoreName;
        this.editOwner = editOwner;
        this.editStreet = editStreet;
        this.editHouseNumber = editHouseNumber;
        this.editZip = editZip;
        this.editTelephone = editTelephone;
        this.editEmail = editEmail;
    }


    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(\\w+\\.)(com|de)$";
        return email.matches(emailRegex);
    }

    public enum ZipCode {
        ZIP_40808("49808"),
        ZIP_49809("49809"),
        ZIP_49811("49811");

        private final String value;

        ZipCode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static boolean isValidZipCode(String zip) {
        for (ValidationManager.ZipCode validZip : ValidationManager.ZipCode.values()) {
            if (validZip.getValue().equals(zip)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInputValid(String storeName, String owner, String street, String houseNumber,
                                       String zip, String telephone, String email) {
        boolean isValid = true;

        if (storeName.trim().isEmpty()) {
            isValid = false;
            editStoreName.setError("Bitte geben Sie einen gültigen Geschäftsnamen ein");
        }

        if (owner.trim().isEmpty() || isNumeric(owner.trim())) {
            isValid = false;
            editOwner.setError("Bitte geben Sie einen gültigen Eigentümer ein");
        }

        if (street.trim().isEmpty() || isNumeric(street.trim())) {
            isValid = false;
            editStreet.setError("Bitte geben Sie eine gültige Straße ein");
        }

        if (houseNumber.trim().isEmpty()) {
            isValid = false;
            editHouseNumber.setError("Bitte geben Sie eine gültige Hausnummer ein");
        }

        if (!isValidZipCode(zip)) {
            isValid = false;
            editZip.setError("Bitte geben Sie eine gültige PLZ ein (49808, 49809, 49811)");
        }

        if (telephone.trim().isEmpty()) {
            isValid = false;
            editTelephone.setError("Bitte geben Sie eine gültige Telefonnummer ein");
        }

        if (!isValidEmail(email)) {
            isValid = false;
            editEmail.setError("Bitte geben Sie eine gültige E-Mail-Adresse ein");
        }

        return isValid;
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // Prüft, ob die Zeichenfolge eine Zahl ist
    }
}
