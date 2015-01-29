import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.UUID.randomUUID;


public class GameManager {
    public static final GameManager GAME_MANAGER = new GameManager();

    Runnable checkThread;

    private GameManager(){
        gameSet = new ConcurrentHashMap<String, Battle>();

        checkThread = new Runnable() {
            @Override
            public void run() {
                while (GameManager.this != null){
                    for (Map.Entry<String, Battle> battleEntry : gameSet.entrySet()){
                        if (battleEntry.getValue().getBattleState() != BattleState.Failed && battleEntry.getValue().requestTimeout()){
                            battleEntry.getValue().setBattleState(BattleState.Failed);
                        }
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(checkThread).start();
    }

    private ConcurrentHashMap<String, Battle> gameSet;


    public JSONObject createNewBattle(GameType gameType, String newGameName, int fieldWidth, int fieldLength, int fieldHeight, int shipsCount){

        Battle newBattle;

        if (gameType == GameType.PUBLIC){
            newBattle = new Battle(getNewPublicName(), fieldWidth, fieldLength, fieldHeight, shipsCount);
            gameSet.put(newBattle.getName(), newBattle);
            newBattle.setBattleState(BattleState.CreatedNewBattle);
            return newBattle.generateStateResponse();
        }

        if (gameType == GameType.NAMED){
            if (isNewGameNameAlreadyExist(newGameName)){
                JSONObject outJson = new JSONObject();
                outJson.put("message type", "fail message");
                outJson.put("result", "fail");
                outJson.put("reason", "game name already exist");
                return outJson;
            }
            newBattle = new Battle(newGameName, fieldWidth, fieldLength, fieldHeight, shipsCount);
            gameSet.put(newBattle.getName(), newBattle);
            newBattle.setBattleState(BattleState.CreatedNewBattle);
            return newBattle.generateStateResponse();
        }

        JSONObject outJson = new JSONObject();
        outJson.put("message type", "fail message");
        outJson.put("result", "fail");
        return  outJson;
    }

    public JSONObject joinToBattle(GameType gameType, String gameName)
    {
        if (gameType == GameType.PUBLIC)
        {
            for (Map.Entry<String, Battle> battleEntry : gameSet.entrySet()){
                if (battleEntry.getValue().getBattleState() != BattleState.Failed  && !battleEntry.getValue().isClientConncted()){
                    return battleEntry.getValue().connectClient();
                }
            }

            JSONObject outJson = new JSONObject();
            outJson.put("message type", "fail message");
            outJson.put("result", "no free game");
            return  outJson;
        }

        if (gameType == GameType.NAMED){
            Battle battle = gameSet.get(gameName);
            if (battle != null){
                return battle.connectClient();
            }
            JSONObject outJson = new JSONObject();
            outJson.put("message type", "fail message");
            outJson.put("result", "No such game");
            return  outJson;
        }
        JSONObject outJson = new JSONObject();
        outJson.put("message type", "fail message");
        outJson.put("result", "join fail");
        return  outJson;
    }


    private boolean isNewGameNameAlreadyExist(String newGameName) {

        return false;
    }

    private String getNewPublicName() {
        return randomUUID().toString();
    }


    public JSONObject setGameState(String gameName, String newNetState) {

        Battle battle = gameSet.get(gameName);

        if (battle != null){
            return battle.setNewState(newNetState);
        }

        JSONObject outJson = new JSONObject();
        outJson.put("message type", "fail message");
        outJson.put("result", "no game find");
        return  outJson;
    }

    public JSONObject getGameState(String gameName) {
        Battle battle = gameSet.get(gameName);

        if (battle != null){
            return battle.generateStateResponse();
        }

        JSONObject outJson = new JSONObject();
        outJson.put("message type", "fail message");
        outJson.put("result", "no game find");
        return  outJson;
    }
}
