package scenario.application.api;

public interface TestScenarioAPI {
    void test();

    static String getScenarioFilePath() {
        String pathToScenarioFile = System.getenv("PATH_SCENARIO_FILE");

        if (pathToScenarioFile == null) {
            throw new RuntimeException("PATH_SCENARIO_FILE environment variable is not set");
        }

        return pathToScenarioFile;
    }

    static String getPeerName() {
        String peerName = System.getenv("PEER_NAME");

        if (peerName == null) {
            throw new RuntimeException("PEER_NAME environment variable is not set");
        }

        return peerName;
    }
}
