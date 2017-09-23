package com.gaojy.cache.voltdb.core.utils;

import org.voltdb.client.ClientResponse;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public interface BulkLoaderErrorHandler
{
	public boolean handleError(RowWithMetaData metaData, ClientResponse response, String error);

	public boolean hasReachedErrorLimit();
}
