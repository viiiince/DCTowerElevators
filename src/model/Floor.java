package model;

import static model.ButtonState.*;

public class Floor {

    private int no;
    private ButtonState buttonState = READY;

    public Floor(int no) {
        this.no = no;
    }


    public ButtonState getButtonState() {
        return buttonState;
    }

    public void setButtonState(ButtonState buttonState) {
        this.buttonState = buttonState;
    }

}
