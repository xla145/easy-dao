package cn.assist.easydao.pojo;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 记录 主要用于存放map形式的数据，一个通用的Model key 统一用驼峰命名，
 * @author xla
 */
public class RecordPojo implements Serializable {


    private static final long serialVersionUID = 1L;

    private Map<String, Object> columns ;

    public Map<String, Object> getColumns() {
        return columns;
    }

    public RecordPojo() {
        columns = new HashMap<>();
    }

    /**
     * Set columns value with map.
     * @param columns 列对象信息
     * @return RecordPojo
     */
    public RecordPojo setColumns(Map<String, Object> columns) {
        this.getColumns().putAll(columns);
        return this;
    }

    /**
     * Remove attribute of this RecordPojo.
     * @param column the column name of the RecordPojo
     * @return RecordPojo
     */
    public RecordPojo remove(String column) {
        getColumns().remove(column);
        return this;
    }


    /**
     * Remove columns of this record.
     * @param columns the column names of the record
     * @return RecordPojo
     */
    public RecordPojo remove(String... columns) {
        if (columns == null) {
            return this;
        }
        Arrays.stream(columns).forEach(c -> this.getColumns().remove(c));
        return this;
    }

    /**
     * Remove columns if it is null.
     */
    public RecordPojo removeNullValueColumns() {
        Iterator<Map.Entry<String, Object>> it = getColumns().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> e = it.next();
            if (e.getValue() == null) {
                it.remove();
            }
        }
        return this;
    }


    /**
     * Remove all columns of this record.
     */
    public RecordPojo clear() {
        getColumns().clear();
        return this;
    }

    /**
     * Set column to record.
     * @param column the column name
     * @param value the value of the column
     */
    public RecordPojo set(String column, Object value) {
        getColumns().put(column, value);
        return this;
    }

    /**
     * Get column of any mysql type
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String column) {
        return (T)getColumns().get(column);
    }


    /**
     * Get column of mysql type: varchar, char, enum, set, text, tinytext, mediumtext, longtext
     */
    public String getStr(String column) {
        Object s = getColumns().get(column);
        return s != null ? s.toString() : null;
    }

    /**
     * Get column of mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
     * @param column 列名
     * @return 整型数据
     */
    public Integer getInt(String column) {
        Number n = getNumber(column);
        return n != null ? n.intValue() : null;
    }

    /**
     * Get column of mysql type: bigint
     */
    public Long getLong(String column) {
        Number n = getNumber(column);
        return n != null ? n.longValue() : null;
    }

    /**
     * Get column of mysql type: unsigned bigint
     */
    public BigInteger getBigInteger(String column) {
        return (BigInteger)getColumns().get(column);
    }

    /**
     * Get column of mysql type: date, year
     */
    public Date getDate(String column) {
        return (Date)getColumns().get(column);
    }


    /**
     * Get column of mysql type: real, double
     */
    public Double getDouble(String column) {
        Number n = getNumber(column);
        return n != null ? n.doubleValue() : null;
    }

    /**
     * Get column of mysql type: float
     */
    public Float getFloat(String column) {
        Number n = getNumber(column);
        return n != null ? n.floatValue() : null;
    }

    /**
     * Get column of mysql type: bit, tinyint(1)
     */
    public Boolean getBoolean(String column) {
        return (Boolean)getColumns().get(column);
    }

    /**
     * Get column of mysql type: decimal, numeric
     */
    public BigDecimal getBigDecimal(String column) {
        return (BigDecimal)getColumns().get(column);
    }

    /**
     * Get column of any type that extends from Number
     */
    public Number getNumber(String column) {
        return (Number)getColumns().get(column);
    }


    /**
     * Return column names of this record.
     */
    public String[] getColumnNames() {
        Set<String> attrNameSet = getColumns().keySet();
        return attrNameSet.toArray(new String[attrNameSet.size()]);
    }

    /**
     * Return column values of this record.
     */
    public Object[] getColumnValues() {
        java.util.Collection<Object> attrValueCollection = getColumns().values();
        return attrValueCollection.toArray(new Object[attrValueCollection.size()]);
    }


    public String getString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        for (Map.Entry<String, Object> e : getColumns().entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            Object value = e.getValue();
            if (value != null) {
                value = value.toString();
            }
            sb.append(e.getKey()).append(':').append(value);
        }
        sb.append('}');
        return sb.toString();
    }
}
