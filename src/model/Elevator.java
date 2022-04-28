package model;


import java.util.ArrayList;
import java.util.List;
import static model.ButtonState.*;
import static model.ElevatorState.*;
import static program.Program.floors;

public class Elevator implements Runnable {

    private final int id;
    private int currentFloor = 0;
    private ElevatorState elevatorState = IDLE;
    private List<Integer> floorsToDo = new ArrayList<>();
    private static int FLOORS_TO_SLOW_DOWN;

    public Elevator(int id) {
        this.id = id;
    }

    public static void setFloorsToSlowDown(int floorsToSlowDown) {
        FLOORS_TO_SLOW_DOWN = floorsToSlowDown;
    }




    @Override
    public void run() {

        while (true) {

            if (elevatorState == GOING_UP) {
                moveUp();
            } else if (elevatorState == GOING_DOWN) {
                moveDown();
            } else if (elevatorState == STOPPED_GOING_UP || elevatorState == STOPPED_GOING_DOWN) {
                System.out.printf("Lift %s: Tür auf/zu Stock %d.\n", id, currentFloor);
                if (currentFloor != 0) {
                    floors.get(currentFloor).setButtonState(READY);
                    System.out.printf("Stock %d: Rufknopf bereit.\n", currentFloor);
                }


                floorsToDo.remove(floorsToDo.get(0));
                if (floorsToDo.isEmpty()) {
                    elevatorState = IDLE;
                    System.out.printf("Lift %d: fertig.\n", id);
                } else {
                    if (currentFloor < floorsToDo.get(0)) {
                        elevatorState = GOING_UP;
                    } else {
                        elevatorState = GOING_DOWN;
                    }
                }

            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    private void moveUp() {

        currentFloor++;
        System.out.printf("Lift %d: Stock %d [Nächster Halt: %d]\n", id, currentFloor, floorsToDo.get(0));
        if (floorsToDo.get(0) == currentFloor) {
            elevatorState = STOPPED_GOING_UP;

        }
    }

    private void moveDown() {

        currentFloor--;
        System.out.printf("Lift %d: Stock %d [Nächster Halt: %d]\n", id, currentFloor, floorsToDo.get(0));
        if (floorsToDo.get(0) == currentFloor) {
            elevatorState = STOPPED_GOING_DOWN;
        }

    }


    public boolean call(ElevatorTask elevatorTask) {

        switch (elevatorState) {
            case IDLE:
                addToDos(elevatorTask);
                return true;
            case GOING_DOWN:
                if (currentFloor - FLOORS_TO_SLOW_DOWN >= elevatorTask.getStartFloor()) {
                    addToDos(elevatorTask);
                    return true;
                }
            case STOPPED_GOING_DOWN:
                if (currentFloor > elevatorTask.getStartFloor()) {
                    addToDos(elevatorTask);
                    return true;
                }
            default:
                return false;
        }
    }

    private void addToDos(ElevatorTask elevatorTask) {
        int _startFloor = Integer.valueOf(elevatorTask.getStartFloor());
        int _destFloor = Integer.valueOf(elevatorTask.getDestFloor());
        int _size = floorsToDo.size();

        switch (elevatorTask.getDirection()) {
            case UP:
                floorsToDo.add(0);
                floorsToDo.add(_destFloor);
                elevatorState = STOPPED_GOING_UP;
                break;

            case DOWN:
                if (currentFloor == 0) {
                    floorsToDo.add(_startFloor);
                    floorsToDo.add(_destFloor);
                    elevatorState = GOING_UP;
                } else if (_size == 0) {
                    if (currentFloor != _startFloor) {
                        floorsToDo.add(_startFloor);
                        floorsToDo.add(_destFloor);
                        if (currentFloor < floorsToDo.get(0)) {
                            elevatorState = GOING_UP;
                        } else {
                            elevatorState = GOING_DOWN;
                        }
                    } else {
                        floorsToDo.add(currentFloor);
                        floorsToDo.add(_destFloor);
                        if (currentFloor < floorsToDo.get(1)) {
                            elevatorState = STOPPED_GOING_UP;
                        } else {
                            elevatorState = STOPPED_GOING_DOWN;
                        }
                    }
                } else if (_size == 1) {
                    floorsToDo.add(0, _startFloor);
                } else {
                    for (int i = 0; i < _size - 1; i++) {
                        if (_startFloor < floorsToDo.get(i) && _startFloor > floorsToDo.get(i + 1)) {
                            floorsToDo.add(i + 1, _startFloor);
                            break;
                        }
                    }
                }
        }
    }


    public int getCurrentFloor() {
        return currentFloor;
    }

    public ElevatorState getElevatorState() {
        return elevatorState;
    }

    public int getId() {
        return id;
    }

}
