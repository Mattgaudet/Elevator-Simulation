package config;

public interface ProcessingHandler {

    /**
     * Completes the processing, transitioning the system or component to a specific state.
     * The implementation should handle the necessary actions or state transitions.
     */
    void completeProcessing();

    /**
     * Starts the processing, initiating actions or state transitions in the system or component.
     * The implementation should perform the required setup or state changes for processing.
     */
    void startProcessing();
}
