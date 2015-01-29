import org.json.JSONObject;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;


enum GameType {PUBLIC, NAMED }

enum BattleState{ServerTurn, ClientTurn, SetupWait, CreatedNewBattle, Failed}

public class Battle {

    private String name;

    private BattleState battleState;

    LocalTime lastRequestTime;

    private boolean ClientConncted;
    private boolean ClientReady;
    private boolean ServerReady;

    private int fieldWidth;
    private int fieldLength;
    private int fieldHeight;

    private int shipsCount;

    private int lastMoveWidth;
    private int lastMoveLength;
    private int lastMoveHeight;

    public Battle(String gameName, int width, int length, int height, int shipsCount){
        name = gameName;
        ClientConncted = false;

        fieldWidth = width;
        fieldLength = length;
        fieldHeight = height;

        this.shipsCount = shipsCount;
    }

    public String getName() {
        return name;
    }

    public BattleState getBattleState() {
        return battleState;
    }

    public void setBattleState(BattleState battleState) {
        this.battleState = battleState;
    }

    public JSONObject generateStateResponse() {

        JSONObject outJson = new JSONObject();

        outJson.put("message type", "battle state response");
        outJson.put("battle name", name);
        outJson.put("battle state", battleState);

        outJson.put("field width", fieldWidth);
        outJson.put("field length", fieldLength);
        outJson.put("field height", fieldHeight);

        outJson.put("ships count", shipsCount);

        outJson.put("last move width", lastMoveWidth);
        outJson.put("last move length", lastMoveLength);
        outJson.put("last move height", lastMoveHeight);

        lastRequestTime = LocalTime.now();
        return outJson;
    }

    public JSONObject setNewState(String newBattleState) {

        if (newBattleState.equals("SetupWait")){
            battleState = BattleState.SetupWait;
        }

        return generateStateResponse();
    }

    public JSONObject connectClient()
    {
        ClientConncted = true;

        return generateStateResponse();
    }

    public boolean isClientConncted() {
        return ClientConncted;
    }

    public boolean requestTimeout() {

        LocalTime checkTime = LocalTime.now();

        if (lastRequestTime == null){
            return  false;
        }
        long timeout = checkTime.until(lastRequestTime, ChronoUnit.SECONDS);

        if (timeout < -50){
            battleState = BattleState.Failed;
            return true;
        }
        return false;
    }
}
