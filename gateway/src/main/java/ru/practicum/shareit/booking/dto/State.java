package ru.practicum.shareit.booking.dto;


public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State fromString(String parameterName) {
        if (parameterName != null) {
            for (State state : State.values()) {
                if (parameterName.equalsIgnoreCase(state.toString().toLowerCase())) {
                    return state;
                }
            }
        }
        return null;
    }
}



