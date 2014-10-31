package me.libraryaddict.halloween;

public class HalloweenApi {
    private static GameManager gameManager;
    private static Halloween mainPlugin;
    private static MysqlManager mysqlManager;
    private static PlayerManager playerManager;

    public static GameManager getGameManager() {
        if (gameManager == null) {
            gameManager = new GameManager();
        }
        return gameManager;
    }

    public static Halloween getMainPlugin() {
        return mainPlugin;
    }

    public static MysqlManager getMysqlManager() {
        if (mysqlManager == null) {
            mysqlManager = new MysqlManager();
        }
        return mysqlManager;
    }

    public static PlayerManager getPlayerManager() {
        if (playerManager == null) {
            playerManager = new PlayerManager();
        }
        return playerManager;
    }

    protected static void setMainPlugin(Halloween halloween) {
        mainPlugin = halloween;
    }
}
