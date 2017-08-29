package io.pivotal.cla.webdriver.smoke;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Rob Winch
 * @since 5.0
 */
public class BrowserErrors implements TestRule {
	private final BrowserWebDriverContainer webDriver;
	private final String name;
	public BrowserErrors(String name, BrowserWebDriverContainer webDriver) {
		this.name = name;
		this.webDriver = webDriver;
	}

	public void writeHtml(File file) throws IOException {
		FileUtils.write(file,
				webDriver.getWebDriver().getPageSource(),
				Charset.defaultCharset());
	}

	@Override
	public Statement apply(Statement base, Description description) {
		return new BrowserErrorsStatement(base);
	}

	class BrowserErrorsStatement extends Statement {
		private final Statement next;

		BrowserErrorsStatement(Statement next) {
			this.next = next;
		}

		public void evaluate() throws Throwable {
			try {
				this.next.evaluate();
			} catch(Throwable t) {
				writeHtml(new File(new File("build"), name+".html"));
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println("==========================");
				System.out.println(webDriver.getWebDriver().getCurrentUrl());
				System.out.println(webDriver.getWebDriver().getTitle());
				System.out.println(webDriver.getWebDriver().getPageSource());
				System.out.println("==========================");
				System.out.println();
				System.out.println();
				System.out.println();
				throw t;
			}
		}
	}
}
