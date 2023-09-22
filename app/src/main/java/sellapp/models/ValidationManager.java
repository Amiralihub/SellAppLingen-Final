package sellapp.models;

import android.annotation.SuppressLint;
import android.widget.EditText;

public class ValidationManager
    {

    @SuppressLint("StaticFieldLeak")
    private static EditText editStoreName;
    @SuppressLint("StaticFieldLeak")
    private static EditText editOwner;
    @SuppressLint("StaticFieldLeak")
    private static EditText editStreet;
    @SuppressLint("StaticFieldLeak")
    private static EditText editHouseNumber;
    @SuppressLint("StaticFieldLeak")
    private static EditText editZip;
    @SuppressLint("StaticFieldLeak")
    private static EditText editTelephone;
    @SuppressLint("StaticFieldLeak")
    private static EditText editEmail;


    public ValidationManager(
            EditText editStoreName,
            EditText editOwner,
            EditText editStreet,
            EditText editHouseNumber,
            EditText editZip,
            EditText editTelephone,
            EditText editEmail
                            )
        {
        ValidationManager.editStoreName = editStoreName;
        ValidationManager.editOwner = editOwner;
        ValidationManager.editStreet = editStreet;
        ValidationManager.editHouseNumber = editHouseNumber;
        ValidationManager.editZip = editZip;
        ValidationManager.editTelephone = editTelephone;
        ValidationManager.editEmail = editEmail;
        }


    public static boolean isValidEmail(String email)
        {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(\\w+\\.)(com|de)$";
        return email.matches(emailRegex);
        }

    /**
     * This enum represents a list of ZIP codes.
     */
    public enum ZipCode
        {
            /**
             * ZIP code 49808 for Lingen.
             */
            ZIP_49808("49808"),

            /**
             * ZIP code 49809 for Lingen.
             */
            ZIP_49809("49809"),

            /**
             * ZIP code 49811 for Lingen.
             */
            ZIP_49811("49811");

        private final String value;

        /**
         * Initializes a new ZIP code with the given value.
         *
         * @param value The ZIP code value.
         */
        ZipCode(String value)
            {
            this.value = value;
            }

        /**
         * Gets the value of the ZIP code.
         *
         * @return The ZIP code value.
         */
        public String getValue()
            {
            return value;
            }
        }

    public static boolean isValidZipCode(String zip)
        {
        for (ValidationManager.ZipCode validZip : ValidationManager.ZipCode.values())
            {
            if (validZip.getValue().equals(zip))
                {
                return true;
                }
            }
        return false;
        }

    public static boolean isInputValid(
            String storeName,
            String owner,
            String street,
            String houseNumber,
            String zip,
            String telephone,
            String email
                                      )
        {
        boolean isValid = true;

        if (storeName.trim().isEmpty())
            {
            isValid = false;
            editStoreName.setError("Bitte geben Sie einen gültigen Geschäftsnamen ein");
            }

        if (owner.trim().isEmpty() || !isNumeric(owner.trim()))
            {
            isValid = false;
            editOwner.setError("Bitte geben Sie einen gültigen Eigentümer ein");
            }

        if (street.trim().isEmpty())
            {
            isValid = false;
            editStreet.setError("Bitte geben Sie eine gültige Straße ein");
            }

        if (houseNumber.trim().isEmpty())
            {
            isValid = false;
            editHouseNumber.setError("Bitte geben Sie eine gültige Hausnummer ein");
            }

        if (!isValidZipCode(zip))
            {
            isValid = false;
            editZip.setError("Bitte geben Sie eine gültige PLZ ein (49808, 49809, 49811)");
            }

        if (houseNumber.trim().isEmpty() || !isMixNumeric(telephone.trim()))
            {
            isValid = false;
            editTelephone.setError("Bitte geben Sie eine gültige Telefonnummer ein");
            }

        if (!isValidEmail(email))
            {
            isValid = false;
            editEmail.setError("Bitte geben Sie eine gültige E-Mail-Adresse ein");
            }
        return isValid;
        }

    private static boolean isNumeric(String str)
        {
        return str.matches("^[\\p{L} \\p{Zs}]+$");
        }



    private static boolean isMixNumeric(String str)
        {
        return str.matches("-?\\d+(\\.\\d+)?");
        }
    }