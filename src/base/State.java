package base;

import enums.Movement;

public class State {

    private Movement movement;

    public State(Movement movement) {
        this.movement = movement;
    }

    public Movement getMovement() {
        return movement;
    }

}
