import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class SpaceBattleServlet extends WebSocketServlet {
    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {

        final String id = request.getParameter("id");

        return new WebSocket.OnTextMessage(){

            private Connection connection = null;

            @Override
            public void onOpen(Connection connection) {
                this.connection = connection;
            }

            @Override
            public void onClose(int i, String s) {

            }

            @Override
            public void onMessage(String message) {
                try {
                    connection.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
