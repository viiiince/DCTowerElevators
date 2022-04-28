package program;

import model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static model.Direction.DOWN;
import static model.ButtonState.*;
import static model.ElevatorState.*;

public class Program {

    private static final int NUMBER_ELEVATORS = 7;
    // Es wird angenommen, dass bei der Stockwerkangabe das Erdgeschoß mitgezählt wird:
    private static final int MAX_FLOOR = 54; // 0 = Erdgeschoß, also 55 Stockwerke
    private static final int FLOORS_TO_SLOW_DOWN = 2;
    static Elevator[] elevators = new Elevator[NUMBER_ELEVATORS];
    private static final Thread[] threads = new Thread[NUMBER_ELEVATORS];
    private static List<ElevatorTask> syncElevatorTasks = Collections.synchronizedList(new ArrayList<>());
    public static List<Floor> floors = new ArrayList<>();


    public static void main(String[] args) {

        for (int i = 0; i < NUMBER_ELEVATORS; i++) {
            threads[i] = new Thread(elevators[i] = new Elevator(i));
            threads[i].setDaemon(true);
            threads[i].start();
        }

        for (int i = 0; i <= MAX_FLOOR; i++) {
            floors.add(i, new Floor(i));
        }

        Elevator.setFloorsToSlowDown(FLOORS_TO_SLOW_DOWN);

        Thread taskGenerator = new Thread(new TaskGenerator(MAX_FLOOR));
        taskGenerator.setDaemon(true);
        taskGenerator.start();


        while (true) {

            if (syncElevatorTasks.size() > 0) {
                ElevatorTask elevatorTask = syncElevatorTasks.get(0);
                if (!(elevatorTask.getDirection() == DOWN && catchMovingElevator(elevatorTask))) {
                    callIdleElevator(elevatorTask);
                }
            }


            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    static void addRequest(int startFloor, int destFloor) {

        if (((destFloor == 0 && startFloor > 0 && startFloor <= MAX_FLOOR) || (startFloor == 0 && destFloor > 0 && destFloor <= MAX_FLOOR))
                && floors.get(startFloor).getButtonState() == READY) {
            if (startFloor != 0) {
                floors.get(startFloor).setButtonState(BLOCKED);
                System.out.printf("Stock %d: Rufknopf blockiert.\n", startFloor);
            }
            syncElevatorTasks.add(new ElevatorTask(startFloor, destFloor));
        } else {
            System.out.println("Anfrage ignoriert!");
        }
    }


    // Checkt, ob schon ein passender Aufzug die gleiche Richtung unterwegs ist (macht nur fürs Runterfahren Sinn):
    private static boolean catchMovingElevator(ElevatorTask _elevatorTask) {

        boolean elevatorCalled = false;

        for (Elevator _elevator : elevators) {

            ElevatorState _elevatorState = _elevator.getElevatorState();
            int _currentFloor = _elevator.getCurrentFloor();

            if ((_elevatorState == GOING_DOWN || _elevatorState == STOPPED_GOING_DOWN)
                    && (_elevatorState == GOING_DOWN ? _currentFloor - FLOORS_TO_SLOW_DOWN : _currentFloor) > _elevatorTask.getDestFloor()) {

                if (_elevator.call(_elevatorTask)) {
                    System.out.printf("Anzeige Stock %s: \"Lift Nr. %s -> Stock %s\"\n",
                            _elevatorTask.getStartFloor(), _elevator.getId(), _elevatorTask.getDestFloor());
                    syncElevatorTasks.remove(0);
                    elevatorCalled = true;
                    break;
                }
            }
        }
        return elevatorCalled;
    }


    private static void callIdleElevator(ElevatorTask _elevatorTask) {
        for (Elevator _elevator : elevators) {
            if (_elevator.getElevatorState() == IDLE && _elevator.call(_elevatorTask)) {
                System.out.printf("Anzeige Stock %s: \"Lift Nr. %s -> Stock %s\"\n",
                        _elevatorTask.getStartFloor(), _elevator.getId(), _elevatorTask.getDestFloor());
                syncElevatorTasks.remove(0);
                break;
            }
        }
    }


}