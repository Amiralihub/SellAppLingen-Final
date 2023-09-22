package sellapp.models;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;

public class EmojiExcludeFilter implements InputFilter
    {
    @Override
    public CharSequence filter(
            CharSequence source,
            int start,
            int end,
            Spanned dest,
            int dstart,
            int dend
                              )
        {
        StringBuilder filtered = new StringBuilder();
        for (int i = start; i < end; i++)
            {
            int type = Character.getType(source.charAt(i));
            if (type != Character.SURROGATE && type != Character.OTHER_SYMBOL)
                {
                filtered.append(source.charAt(i));
                }
            }
        return (source instanceof Spanned) ? new SpannableString(filtered) : filtered.toString();
        }
    }
