package com.gaojy.cache.voltdb.core.commons;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.voltdb.types.TimestampType;
import org.voltdb.utils.Encoder;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public enum ImporterType
{

	BOOLEAN(TypeToken.of(Boolean.class), TypeToken.of(Boolean.TYPE))
	{
		@Override
		public Acceptor getAcceptorFor(ResultSet rslt, int idx)
		{
			return new Acceptor(rslt, idx)
			{
				@Override
				public Object convert() throws SQLException
				{
					Object val = m_rslt.getObject(m_idx);
					if (m_rslt.wasNull())
						return null;
					return (Boolean) val ? (byte) 1 : (byte) 0;
				}
			};
		}
	},
	BYTE(TypeToken.of(Byte.class), TypeToken.of(Byte.TYPE)), CHARACTER(TypeToken.of(Character.class), TypeToken
			.of(Character.TYPE)), SHORT(TypeToken.of(Short.class), TypeToken.of(Short.TYPE)), INTEGER(TypeToken
			.of(Integer.class), TypeToken.of(Integer.TYPE)), LONG(TypeToken.of(Long.class), TypeToken.of(Long.TYPE)), FLOAT(
			TypeToken.of(Float.class), TypeToken.of(Float.TYPE)), DOUBLE(TypeToken.of(Double.class), TypeToken
			.of(Double.TYPE)), DECIMAL(TypeToken.of(BigDecimal.class)), STRING(TypeToken.of(String.class)), BYTEARRAY(
			TypeToken.of(byte[].class))
	{
		@Override
		public Acceptor getAcceptorFor(ResultSet rslt, int idx)
		{
			return new Acceptor(rslt, idx)
			{
				@Override
				public String format(Object o)
				{
					return o != null ? Encoder.hexEncode((byte[]) o) : "NULL";
				}
			};
		}
	},
	DATE(TypeToken.of(Date.class))
	{
		@Override
		public Acceptor getAcceptorFor(ResultSet rslt, int idx)
		{
			return new Acceptor(rslt, idx)
			{
				final SimpleDateFormat dfmt = new SimpleDateFormat(Constants.ODBC_DATE_FORMAT_STRING);

				@Override
				public Object convert() throws SQLException
				{
					Object val = m_rslt.getObject(m_idx);
					if (m_rslt.wasNull())
						return null;
					return new TimestampType((Date) val);
				}

				@Override
				public String format(Object o)
				{
					return o != null ? dfmt.format(((TimestampType) o).asApproximateJavaDate()) : "NULL";
				}
			};
		}
	},
	BLOB(TypeToken.of(java.sql.Blob.class))
	{
		@Override
		public Acceptor getAcceptorFor(ResultSet rslt, int idx)
		{
			return new Acceptor(rslt, idx)
			{
				@Override
				public Object convert() throws SQLException
				{
					Object val = null;
					java.sql.Blob blob = null;
					try
					{
						val = m_rslt.getObject(m_idx);
						if (m_rslt.wasNull())
							return null;

						blob = (java.sql.Blob) val;
						if (blob.length() > Constants.MAX_COLUMN_SIZE)
						{
							throw new SQLException("blobs may not be greater than " + Constants.MAX_COLUMN_SIZE);
						}
						return blob.getBytes(0, (int) blob.length());
					}
					finally
					{
						if (blob != null)
						{
							try
							{
								blob.free();
							}
							catch (Exception ignoreIt)
							{
							}
						}
					}
				}

				@Override
				public String format(Object o)
				{
					return o != null ? Encoder.hexEncode((byte[]) o) : "NULL";
				}
			};
		}
	};

	public static class Acceptor
	{
		protected final ResultSet m_rslt;
		protected final int m_idx;

		public Acceptor(ResultSet rslt, int idx)
		{
			m_rslt = rslt;
			m_idx = idx;
		}

		/**
		 * Default conversion. return it as is
		 * 
		 * @return return result set object as os
		 * @throws SQLException
		 */
		public Object convert() throws SQLException
		{
			Object val = m_rslt.getObject(m_idx);
			if (m_rslt.wasNull())
				return null;
			return val;
		}

		public String format(Object o)
		{
			return o != null ? o.toString() : "NULL";
		}
	}

	final static class IsAssignableFromChecker implements Predicate<TypeToken<?>>
	{
		private final TypeToken<?> m_from;

		public IsAssignableFromChecker(String clazzName)
		{
			TypeToken<?> token = null;
			try
			{
				token = TypeToken.of(Class.forName(clazzName));
			}
			catch (ClassNotFoundException e)
			{
				// Throwables.propagate(e);
			}
			m_from = token;
		}

		@Override
		public boolean apply(TypeToken<?> input)
		{
			return input.isAssignableFrom(m_from);
		}
	}

	final Set<TypeToken<?>> typeTokens;

	ImporterType(TypeToken<?>... tokens)
	{
		typeTokens = ImmutableSet.copyOf(tokens);
	}

	public Acceptor getAcceptorFor(ResultSet rslt, int idx)
	{
		return new Acceptor(rslt, idx);
	}

	public static ImporterType forClassName(String className)
	{
		IsAssignableFromChecker checker = new IsAssignableFromChecker(className);
		for (ImporterType e : values())
		{
			if (FluentIterable.from(e.typeTokens).anyMatch(checker))
			{
				return e;
			}
		}
		return null;
	}

}
