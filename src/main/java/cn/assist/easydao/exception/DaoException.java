package cn.assist.easydao.exception;

/**
 * 
 * 自定义异常
 * 
 * @author caixb
 *
 */
public class DaoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DaoException(String message) {
		super(message);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}
}
