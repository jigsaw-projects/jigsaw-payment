package org.jigsaw.payment.metric;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月13日
 */
@Aspect
public class TimerAspect {
	private static final Logger localLogger = LoggerFactory
			.getLogger(TimerAspect.class);
	private Map<String, ExecutionTimer<Object>> store = new ConcurrentHashMap<>();
	private GaugeService gaugeService;
	private CounterService counterService;
	private ObjectMapper mapper;

	public class ExecutionTimer<T> {
		protected final String name;
		private final Logger logger;
		private Histogram histogram;

		public ExecutionTimer(GaugeService gaugeService,
				CounterService counterService, String name, Logger logger) {
			this.name = name;
			this.logger = logger;
			this.histogram = new Histogram();
		}

		private synchronized void submit(final long duration) {
			histogram.addValue(duration);
			gaugeService.submit(name + ".last", duration);
			gaugeService.submit(name + ".average",
					Double.valueOf(histogram.getAverage()).longValue());
			gaugeService.submit(name + ".max", histogram.getMax());
			gaugeService.submit(name + ".min", histogram.getMin());
			counterService.increment(name);
		}

		public T measure(ProceedingJoinPoint joinPoint) throws Throwable {
			final StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			@SuppressWarnings("unchecked")
			final T result = (T) joinPoint.proceed();

			stopWatch.stop();
			final long duration = stopWatch.getLastTaskTimeMillis();

			String log = this.buildLog(joinPoint, duration);
			logger.debug(log);
			submit(duration);
			return result;
		}

		private String buildLog(ProceedingJoinPoint joinPoint, long duration) {
			StringBuilder builder = new StringBuilder();
			builder.append(this.name).append(": { tid: ").append(Thread.currentThread().getId())
					.append("; ");
			builder.append("args: [");
			boolean first = true;
			for (Object arg : joinPoint.getArgs()) {
				if (first) {
					first = false;
				} else {
					builder.append("; ");
				}
				builder.append(toLogString(arg));
			}
			builder.append("]; duration:").append(duration).append("}");
			return builder.toString().replace('\n', ' ');
		}

	}

	@Autowired
	public TimerAspect(GaugeService gaugeService,
			CounterService counterService, ObjectMapper mapper) {
		this.counterService = counterService;
		this.gaugeService = gaugeService;
		this.mapper = mapper;
	}

	@Around("@annotation(Timer)")
	public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
		Timer annotation = getTimerAnnotation(joinPoint);
		String metricName = annotation.value();
		Logger targetLogger = LoggerFactory
				.getLogger(getClassForLogger(joinPoint));
		ExecutionTimer<Object> timer = store.computeIfAbsent(metricName,
				(name) -> getTimer(name, targetLogger));

		return timer.measure(joinPoint);
	}

	private Class<?> getClassForLogger(ProceedingJoinPoint joinPoint)
			throws Exception {
		Class<?> loggerClass = AopUtils.getTargetClass(joinPoint.getTarget());
		if (loggerClass.getName().contains("$$EnhancerBySpringCGLIB$$")) {
			loggerClass = loggerClass.getSuperclass();
		}
		return loggerClass;
	}

	private Timer getTimerAnnotation(ProceedingJoinPoint joinPoint)
			throws NoSuchMethodException {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Timer timer = method.getAnnotation(Timer.class);
		if (timer == null && method.getDeclaringClass().isInterface()) {
			final String methodName = signature.getName();
			final Class<?> implementationClass = joinPoint.getTarget()
					.getClass();
			final Method implementationMethod = implementationClass
					.getDeclaredMethod(methodName, method.getParameterTypes());
			timer = implementationMethod.getAnnotation(Timer.class);
		}
		return timer;
	}

	private <T> ExecutionTimer<T> getTimer(String name, Logger logger) {
		return new ExecutionTimer<>(this.gaugeService, this.counterService,
				name, logger);
	}

	private String toLogString(Object arg) {
		if (arg == null)
			return "";
		try {
			return mapper.writeValueAsString(arg);
		} catch (JsonProcessingException e) {
			localLogger.debug("Error in converting " + arg.getClass() + ".");
			return String.valueOf(arg);
		}
	}
}
