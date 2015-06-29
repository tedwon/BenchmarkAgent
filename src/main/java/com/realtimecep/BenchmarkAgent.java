package com.realtimecep;

import java.util.concurrent.TimeUnit;

import io.reactivex.netty.RxNetty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Benchmark Agent Class.
 *
 * <p/>정확한 부하 측정을위한 request injector 도구로 사용할 수 있음.
 *
 * Original SRC: http://svn.codehaus.org/esper/esper/trunk/examples/benchmark/src/main/java/com/espertech/esper/example/benchmark/client/MarketClient.java
 */
public class BenchmarkAgent {

	private final static Logger logger = LoggerFactory.getLogger(BenchmarkAgent.class);

	private final static String URL = "http://lnadpap1501.nhnjp.ism:20080/";
	//	private final static String URL = "http://cnaddev7504.line.ism:20080/";
	//	private final static String URL = "http://localhost:8080/data";

	/**
	 * 1초당 평균 rate 수만큼 데이터 전송하기. <p/>부하 테스트 클라이언트 용도로 사용한다.
	 *
	 * @param rate 1초당 데이터 전송 rate
	 */
	public void run(int rate) {
		int eventPer50ms = rate / 20;
		int tickerIndex = 0;
		int countLast5s = 0;
		int sleepLast5s = 0;
		long lastThroughputTick = System.currentTimeMillis();
		try {
			do {
				long ms = System.currentTimeMillis();
				for (int i = 0; i < eventPer50ms; i++) {

					// TODO: biz code here
					// sender.send(data)

					RxNetty.createHttpGet(URL).forEach(content -> {
					});
					//					RxNetty.createHttpGet(URL)
					//							.map(response -> response.getStatus().code())
					//							.toBlocking()
					//							.forEach(System.out::println);
					//                    RxNetty.createHttpGet("http://localhost:8080/data").
					//                    RxNetty.createHttpGet("http://localhost:8080/data")
					//                        .map(response -> response.getStatus().code())
					//                        .toBlocking()
					//                        .forEach(System.out::println);

					countLast5s++;
					// info
					if (System.currentTimeMillis() - lastThroughputTick > 5 * 1E3) {
						System.out.printf("Sent %d in %d(ms) avg ns/msg %.0f(ns) avg %d(msg/s) sleep %d(ms)\n",
								countLast5s,
								System.currentTimeMillis() - lastThroughputTick,
								(float)1E6 * countLast5s / (System.currentTimeMillis() - lastThroughputTick),
								countLast5s / 5,
								sleepLast5s
						);
						countLast5s = 0;
						sleepLast5s = 0;
						lastThroughputTick = System.currentTimeMillis();
					}
				}
				// rate adjust
				if (System.currentTimeMillis() - ms < 50) {
					// lets avoid sleeping if == 1ms, lets account 3ms for interrupts
					long sleep = Math.max(1, (50 - (System.currentTimeMillis() - ms) - 3));
					sleepLast5s += sleep;
					Thread.sleep(sleep);
				}
			} while (true);
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("Error sending data to server. Did server disconnect?");
		}
	}

	public static void main(String args[]) {

		// 1초당 데이터 전송 rate
		// input stress 수치
		int rate = 1000;

		if (args.length == 1) {
			rate = Integer.parseInt(args[0]);
		}

		logger.info("[Agent] ################################################");
		logger.info("[Agent] ################################################");
		logger.info("[Agent] ### RATE={}", rate);
		logger.info("[Agent] ################################################");
		logger.info("[Agent] ################################################");


		new BenchmarkAgent().run(rate);
	}
}