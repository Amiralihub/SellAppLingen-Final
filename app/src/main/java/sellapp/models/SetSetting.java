package sellapp.models;

public class SetSetting
    {
    public String parameter;
    public String value;

    public SetSetting(String parameter, String value)
        {
        this.parameter = parameter;
        this.value = value;
        }

    @Override
    public String toString()
        {
        return "SetSettings{" + "parameter='" + parameter + '\'' + ", value='" + value + '\'' + '}';
        }
    }