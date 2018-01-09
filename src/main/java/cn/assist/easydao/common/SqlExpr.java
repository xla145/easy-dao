package cn.assist.easydao.common;

/**
 * sql常用表达式
 * 
 * @author caixb
 *
 */
public enum SqlExpr {
    
	EQUAL("="), UNEQUAL("!="), GT(">"), GT_AND_EQUAL(">="), LT("<"), LT_AND_EQUAL("<="), IN("in"), IS_NULL("is null"), IS_NOT_NULL("is not null"), LEFT_LIKE("like"), RIGHT_LIKE("like"), ALL_LIKE("like"), ;
    private String expr ;

    private SqlExpr(String expr) {
        this.expr = expr;
    }

    @Override
    public String toString() {
        return String.valueOf(this.expr);
    }
}
