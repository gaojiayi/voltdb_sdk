package com.ai.aif.cache.voltdb.core.config.node;

public class VoltdbNode
{
	private LocalCacheNode localCache;
	
	private DatasourceNode dataSource;
	
	private IbsNode ibsNode;
	
	private CheckTaskNode  checkTaskNode;
	
	public IbsNode getIbsNode()
	{
		return ibsNode;
	}
	public void setIbsNode(IbsNode ibsNode)
	{
		this.ibsNode = ibsNode;
	}
	public CheckTaskNode getCheckTaskNode()
	{
		return checkTaskNode;
	}
	public void setCheckTaskNode(CheckTaskNode checkTaskNode)
	{
		this.checkTaskNode = checkTaskNode;
	}
	public LocalCacheNode getLocalCache()
	{
		return localCache;
	}
	public void setLocalCache(LocalCacheNode localCache)
	{
		this.localCache = localCache;
	}
	public DatasourceNode getDataSource()
	{
		return dataSource;
	}
	public void setDataSource(DatasourceNode dataSource)
	{
		this.dataSource = dataSource;
	}
	public VoltdbNode()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
