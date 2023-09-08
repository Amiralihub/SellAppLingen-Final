package sellapp.models;

import sellapp.fragments.SettingFragment;

public class ValidationManager
{

    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(\\w+\\.)(com|de)$";
        return email.matches(emailRegex);
    }

    public static boolean isValidZipCode(String zip)
    {
        for (SettingFragment.ZipCode validZip : SettingFragment.ZipCode.values())
        {
            if (validZip.getValue().equals(zip))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isInputValid(String storeName, String owner, String street, String houseNumber, String zip, String telephone, String email)
    {
        boolean isValid = true;

        if (storeName.trim().isEmpty())
        {
            isValid = false;
        }

        if (owner.trim().isEmpty())
        {
            isValid = false;
        }

        if (street.trim().isEmpty())
        {
            isValid = false;
        }

        if (houseNumber.trim().isEmpty())
        {
            isValid = false;
        }

        if (!isValidZipCode(zip))
        {
            isValid = false;
        }

        if (telephone.trim().isEmpty())
        {
            isValid = false;
        }

        if (!isValidEmail(email))
        {
            isValid = false;
        }

        return isValid;
    }
}
