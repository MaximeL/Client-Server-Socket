package fr.maximel.projects.client.server.socket;

import java.util.concurrent.Callable;

public abstract class Function implements Callable<Status> {

    protected Communicator communicator;

    public Function() {
    }

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    abstract public Status call() throws Exception;
}
