package yummy;

import java.util.List;
import java.util.Map;

public class TagHolder {
    private String shortname;
    private String type;
    private Object scalar;
    private List<Object> list;
    private Map<Object,Object> map;

    public TagHolder(String shortname, Object node) {
        this.shortname = shortname;

        if (node instanceof List) {
            this.type = "list";
            this.list = (List<Object>)node;
        } else if (node instanceof Map) {
            this.type = "map";
            this.map = (Map<Object,Object>)node;
        } else {
            this.type = "scalar";
            this.scalar = node;
        }
    }

    public Object             getScalar()    { return scalar; }
    public List<Object>       getList()      { return list; }
    public Map<Object,Object> getMap()       { return map; }
    public String             getType()      { return type; }
    public String             getShortName() { return shortname; }
}
