package model;

import static model.Direction.*;


public class ElevatorTask {

    private int startFloor;
    private int destFloor;
    private Direction direction;

    public ElevatorTask(int startFloor, int destFloor) {
        this.startFloor = startFloor;
        this.destFloor = destFloor;
        direction = (startFloor == 0) ? UP : DOWN;
    }


    public int getStartFloor() {
        return startFloor;
    }

    public int getDestFloor() {
        return destFloor;
    }

    public Direction getDirection() {
        return direction;
    }


}
