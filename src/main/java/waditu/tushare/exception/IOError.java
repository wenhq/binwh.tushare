package waditu.tushare.exception;

public class IOError extends RuntimeException {

	private static final long serialVersionUID = 20191228L;

	public IOError(String message) {
		super(message);
	}

	/*
	 * 重写fillInStackTrace方法会使得这个自定义的异常不会收集线程的整个异常栈信息 减少异常开销。
	 */
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
