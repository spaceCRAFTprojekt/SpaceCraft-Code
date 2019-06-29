package blocks;

import java.io.Serializable;
import java.util.HashMap;
/**
 * Metadaten für eine Position in der Map (nicht an einen Block gebunden)
 */
public class Meta extends HashMap<String, Object> implements Serializable
{
    public static final long serialVersionUID=0L;
}