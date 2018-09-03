package fr.polytechnice.templateapi;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class TemplateApi {
    private static final Logger logger = LoggerFactory.getLogger(TemplateApi.class);

    private final int port;
    private final HttpServer server;

    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();

        try {
            int port = StringUtils.isNotEmpty(System.getenv("PORT")) ? Integer.parseInt(System.getenv("PORT")) : 8080;

            TemplateApi api = new TemplateApi(port);
            api.start();
        } catch (Throwable e) {
            logger.error("Couldn't start server: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    public TemplateApi(int port) {
        URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();
        ResourceConfig config = new ResourceConfig();
        config.register(new LoggingFeature(java.util.logging.Logger.getLogger(this.getClass().getName()), Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT, 8192));
        config.register(JacksonJaxbJsonProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
        config.packages(this.getClass().getPackage().getName() + ".providers");
        config.packages(this.getClass().getPackage().getName() + ".resources");

        this.port = port;
        this.server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config, false);
    }

    public void start() throws IOException {
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.shutdown().get();
            } catch (ExecutionException e) {
                logger.error("Error while shutting down server: {}", e.getCause().getMessage(), e.getCause());
            } catch (InterruptedException e) { /* noop */ }
        }));

        logger.info("Server started on port {}", port);
    }
}
