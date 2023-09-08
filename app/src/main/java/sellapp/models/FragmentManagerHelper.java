package customerapp.models.customerapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Helper class to manage fragment transactions, offering methods to replace fragments and handle custom animations.
 */
public class FragmentManagerHelper
{

    /**
     * Replaces the existing fragment with the provided one in the specified frame.
     *
     * @param fragmentManager The fragment manager.
     * @param frameId         The ID of the frame where the fragment should be placed.
     * @param fragment        The new fragment to be placed.
     */
    public static void replace(FragmentManager fragmentManager, int frameId, Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(frameId, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Navigates to a specified fragment with custom animations.
     *
     * @param fragmentManager The fragment manager.
     * @param frameId         The ID of the frame where the fragment should be placed.
     * @param fragment        The new fragment to navigate to.
     * @param enterAnim       Animation resource ID for the animation applied to the entering fragment.
     * @param exitAnim        Animation resource ID for the animation applied to the exiting fragment.
     * @param addToBackStack  If true, the transaction will be added to the back stack, allowing it to be reversed.
     */
    public static void goToFragment(FragmentManager fragmentManager, int frameId, Fragment fragment, int enterAnim, int exitAnim, boolean addToBackStack)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnim, exitAnim);
        transaction.replace(frameId, fragment);
        if (addToBackStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * Navigates back to the previous fragment in the back stack.
     *
     * @param fragmentManager The fragment manager.
     */
    public static void goBackToPreviousFragment(FragmentManager fragmentManager)
    {
        if (fragmentManager != null)
        {
            fragmentManager.popBackStack();
        }
    }
}
