package net.drapuria.framework.proxy.util.option.impl;



import net.drapuria.framework.proxy.util.option.OptionContext;

import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class SwitchableOptionContext<C, T> extends OptionContext<T, C, T> {

    private List<T> inputList;
    private int selectedIndex;
    private List<String> namedOptions;

    public SwitchableOptionContext(String optionName, C context, Class<T> typeClass, T defaultValue, List<T> inputList, List<String> namedOptions) {
        super(optionName, context, typeClass, defaultValue);
        this.selectedIndex = defaultValue == null ? 0 : inputList.indexOf(defaultValue);
        this.inputList = inputList;
        this.namedOptions = namedOptions;
    }


    @Override
    public boolean validateInput(T input) {
        return true;
    }

    @Override
    public void inputValue(T input) {
        selectedIndex++;
        if(selectedIndex >= inputList.size())
            selectedIndex = 0;
        setValue(inputList.get(selectedIndex));
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public List<T> getInputList() {
        return inputList;
    }

    public List<String> getNamedOptions() {
        return namedOptions;
    }

    @Override
    public void deserializeAndSet(String base64Enc) {
        super.deserializeAndSet(base64Enc);
        selectedIndex = inputList.indexOf(getValue());
    }
}
