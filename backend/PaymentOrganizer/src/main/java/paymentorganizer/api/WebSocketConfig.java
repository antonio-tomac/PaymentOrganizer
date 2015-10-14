package paymentorganizer.api;

import java.io.IOException;
import java.lang.annotation.Annotation;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends
		AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/control").setAllowedOrigins("*").withSockJS();
	}

	@Component
	public static class SimpleCORSFilter implements Filter {

		@Override
		public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
			HttpServletResponse response = (HttpServletResponse) res;
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
			chain.doFilter(req, res);
		}

		@Override
		public void init(FilterConfig filterConfig) {
		}

		@Override
		public void destroy() {
		}

	}

}
