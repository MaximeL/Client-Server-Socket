package fr.maximel.projects.client.server.socket;


public enum Status {

    CONNECTION_ERROR("Connection error"),
    COMMUNICATION_ERROR("Communication error"),
    UNEXPECTED_RECEIVE("Unexpected receive"),
    TIMEOUT("Timeout"),
    UNKNOWN_ERROR("Unknown error"),
    OK("Ok"),
    RUNNING("Running");

    private String value;
    Status(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
