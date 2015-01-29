import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by iyartsev on 14.01.2015.
 */
public class SpaceBattleServer {

    public static void main(String[] args) throws Exception
    {
        String webappDirLocation = "src/main/webapp/";
        Server server = new Server(8082);

        WebAppContext root = new WebAppContext();
        root.setContextPath("/");
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);
        root.setParentLoaderPriority(true);

        server.setHandler(root);
        server.start();
        server.join();
    }
}
