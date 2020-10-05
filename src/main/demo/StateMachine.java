package demo;

import java.util.*;

/**
 * A StateMachine is a class used to model screens in the game.
 *   One state has a unique string name, an onEnter function, an
 *   onUpdate function, and an onExit function. 
 *   One machine can only have one state at a time. 
 */
public class StateMachine {
    private class State {
        public Runnable onEnter;
        public Runnable onUpdate;
        public Runnable onExit;

        public State(Runnable enter, Runnable update, Runnable exit) {
            onEnter = enter;
            onUpdate = update;
            onExit = exit;
        }
    }

    private HashMap<String, State> states;
    private State currentState;

    /**
     * Constructs a state machine. 
     */
    public StateMachine() {
        states = new HashMap<String, State>();
    }

    /**
     * Add a new state to the machine.
     * @modifies this
     * @requires name != null
     * @param name the name of the new state
     * @param enter the function to call upon entering this state
     * @param update the function to call when update this state
     * @param exit the function to call when exiting this state
     */
    public void addState(String name, Runnable enter, Runnable update, Runnable exit) {
        if (name == null) {
            throw new IllegalArgumentException();
        }

        states.put(name, new State(enter, update, exit));
    }

    /**
     * Exit the current state and set the current state to null.
     * @modifies this
     */
    public void shutdown() {
        if (currentState != null && currentState.onExit != null) {
            currentState.onExit.run();
        }
        currentState = null;
    }

    /**
     * Transition to a new state with the given name. 
     * @requires stateName != null
     * @modifies this
     */
    public void transitionTo(String stateName) {
        if (stateName == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (!states.containsKey(stateName)) {
            throw new IllegalArgumentException("no state with name: " + stateName);
        }

        if (currentState != null && currentState.onExit != null) {
            currentState.onExit.run();
        }
        currentState = states.get(stateName);
        if (currentState.onEnter != null) {
            currentState.onEnter.run();
        }
    }

    /**
     * Update the current state of the machine.
     * @effects the current state of the machine might change
     * @return true if the current state is not null and has an update function, false otherwise.
     */
    public boolean update() {
        if (currentState != null && currentState.onUpdate != null) {
            currentState.onUpdate.run();
            return true;
        }
        return false;
    }
}