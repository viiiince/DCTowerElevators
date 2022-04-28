package program;

import java.util.Random;
import static program.Program.addRequest;

public class TaskGenerator implements Runnable {

    private int maxFloor;

    public TaskGenerator(int maxFloor){
        this.maxFloor = maxFloor;
    }

    @Override
    public void run() {

        addRequest(-1, 22); // Test: wird ignoriert
        addRequest(maxFloor + 1, 0); // Test: wird ignoriert
        addRequest(22, -1); // Test: wird ignoriert
        addRequest(0, maxFloor + 1); // Test: wird ignoriert
        addRequest(12, 12); // Test: wird ignoriert
        addRequest(53, 0);
        addRequest(0, 14);
        addRequest(0, 24);
        addRequest(53, 0); // Test: wird ignoriert
        addRequest(50, 0);
        addRequest(0, 49);
        addRequest(0, 48);
        addRequest(34, 0);
        addRequest(28, 0);
        addRequest(0, 33);
        addRequest(0, 30);
        addRequest(maxFloor, 0);
        addRequest(26, 0);
        addRequest(0, 32);
        addRequest(0, 42);
        addRequest(25, 0);
        addRequest(0, 44);
        addRequest(0, 39);
        addRequest(0, 11);
        addRequest(20, 0);
        addRequest(0, 1);
        addRequest(15, 0);

        Random random = new Random();

        while (true) {

            int nextFloorRequest = random.nextInt(maxFloor + 1);
            int nextDirectionRequest = random.nextInt(2);
            if (nextDirectionRequest == 0) {
                addRequest(0, nextFloorRequest);
            } else {
                addRequest(nextFloorRequest, 0);
            }


            try {
                Thread.sleep(random.nextInt(4000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
