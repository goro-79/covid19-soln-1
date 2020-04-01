package live.goro.covid19;

import live.goro.common.security.security.CommonSecurity;
import live.goro.common.security.security.mongodb.CommonUserDetailsService;
import live.goro.common.web.functions.CommonServerFunctions;
import live.goro.common.web.functions.router.CommonRouterFunctions;
import live.goro.covid19.handler.DataHandler;
import live.goro.covid19.handler.ItemHandler;
import live.goro.covid19.handler.OfferedItemRequestHandler;
import live.goro.covid19.handler.UserHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.ServerHttpHeadersWriter;
import org.springframework.security.web.server.header.StaticServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;
import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class Application {
    @Value("${app.login.page:/pages/public/login}")
    private String loginPage;

    @Value("${app.security.public.paths}")
    private Set<String> publicPaths;

    @Value("${app.security.password.encoder.strength:7}")
    private Integer passwordEncodingStrength;

    private String onSuccessPage = "/pages/home";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new ApplicationPidFileWriter("app.pid"));
        application.run(args);
    }

    /* * server http connector */
   // @Bean
    public static Object httpConnector(HttpHandler httpHandler,
                                       @Value("${server.port}") int httpsPort,
                                       @Value("${server.http.port}") int httpPort,
                                       @Value("${GORO_COVID_19_URI:https://localhost}") URI redirectUri) {
        return CommonServerFunctions.getHttpConnector(httpPort,
                redirectUri.toString() + ":" + httpsPort);

    }

    /* * security config * */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity,
                                                         @Value("${app.allow.origin:covid-19.goro.live}") String origin) {
        CommonSecurity.configureDefaultSecurity(serverHttpSecurity, true, publicPaths, loginPage, onSuccessPage);
        //serverHttpSecurity.headers(xframeWriter(origin));
        return serverHttpSecurity.build();
    }

    public Customizer<ServerHttpSecurity.HeaderSpec> xframeWriter(@Value("${app.allow.origin:covid-19.goro.live}") String origin) {
        return header -> {
            StaticServerHttpHeadersWriter writer =
                    StaticServerHttpHeadersWriter.builder()
                            .header("X-FRAME-OPTIONS", String.format("ALLOW-FROM %s", origin))
                            .build();
            header.writer(writer);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return CommonSecurity.bcryptEncoder(passwordEncodingStrength);
    }

    @Bean
    public CommonUserDetailsService userDetailsService(@NonNull MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext,
                                                       @NonNull ReactiveMongoOperations mongoOperation,
                                                       @NonNull PasswordEncoder encoder) {
        return CommonSecurity.getMongoDBUserDetailsService(encoder, mongoOperation, mappingContext);
    }

    /* * web config * */
    // ui router
    @Bean
    public RouterFunction<ServerResponse> resourceRouter() {
        return CommonRouterFunctions.getStaticAndHtmlRouter(null, CommonRouterFunctions.isRoot());
    }

    // api router
    @Bean
    RouterFunction<?> apiRouter(DataHandler dataHandler,
                                UserHandler userHandler,
                                ItemHandler itemHandler,
                                OfferedItemRequestHandler offeredItemRequestHandler) {
        return dataHandler.router()
                .andOther(userHandler.router())
                .andOther(itemHandler.router())
                .andOther(offeredItemRequestHandler.router());
    }

}
