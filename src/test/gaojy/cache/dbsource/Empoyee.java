package test.gaojy.cache.dbsource;

import com.gaojy.cache.voltdb.core.commons.AIColumn;
import com.gaojy.cache.voltdb.core.commons.AIEntity;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
@AIEntity
public class Empoyee
{
	@AIColumn(name="name")
	private String name;
	
	@AIColumn(name="id")
	private String id;

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Empoyee\n{name=");
		builder.append(name);
		builder.append(",id=");
		builder.append(id);
		builder.append("}");
		return builder.toString();
	}
	
}
