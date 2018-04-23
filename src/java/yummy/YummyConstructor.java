package yummy;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class YummyConstructor extends SafeConstructor {

    public void registerTag(String shortname) {
        final String tag = "!" + shortname;
        this.yamlConstructors.put(new Tag(tag), new YummyConstruct(shortname));
    }

    private class YummyConstruct extends AbstractConstruct {
        private String shortname;

        public YummyConstruct(String shortname) {
            this.shortname = shortname;
        }

        private Object innerConstruct(Node node) {
            if (node instanceof ScalarNode) {
                return constructScalar((ScalarNode)node);
            } else if (node instanceof MappingNode) {
                final Map<Object,Object> map = new HashMap<Object,Object>();

                for (NodeTuple tuple: ((MappingNode)node).getValue()) {
                    Object k = innerConstruct(tuple.getKeyNode());
                    Object v = innerConstruct(tuple.getValueNode());
                    map.put(k, v);
                }
                return map;
            } else if (node instanceof CollectionNode) {
                final List<Object> list = new ArrayList<Object>();

                for (Node subnode: ((SequenceNode)node).getValue()) {
                    list.add(innerConstruct(subnode));
                }
                return list;
            } else {
                throw(new IllegalStateException("invalid node type in construct"));
            }
        }

        public Object construct(Node node) {
            return new TagHolder(shortname, innerConstruct(node));
        }
    }
}
