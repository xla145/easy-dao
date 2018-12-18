package cn.assist.easydao.pojo;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * 分页组件--测试版
 * 
 * @version 1.0.0
 * @author caixb
 */
public class PagePojo<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 *  当前页面
	 */
	private int pageNo;
	/**
	 * 每页显示记录数
	 */
	private int pageSize;
	/**
	 * 总行数
	 */
	private int total;
	/**
	 * 总页数
	 */
	private int pageTotal;
	/**
	 * 结果集
	 */
	private List<T> pageData;

	
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	public List<T> getPageData() {
		return pageData;
	}

	public void setPageData(List<T> pageData) {
		this.pageData = pageData;
	}

	@Override
	public String toString() {
		return "PagePojo [pageNo=" + pageNo + ", pageSize=" + pageSize
				+ ", total=" + total + ", pageData=" + pageData + "]";
	}
}
