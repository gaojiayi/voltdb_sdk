package test.common;

import java.io.Serializable;

public class User implements Serializable
{
private String name;
private int id;
public User()
{
	super();
	// TODO Auto-generated constructor stub
}
public String getName()
{
	return name;
}
public void setName(String name)
{
	this.name = name;
}
public int getId()
{
	return id;
}
public void setId(int id)
{
	this.id = id;
}

}
