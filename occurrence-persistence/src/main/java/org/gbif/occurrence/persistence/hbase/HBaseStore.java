package org.gbif.occurrence.persistence.hbase;

import org.gbif.api.exception.ServiceUnavailableException;
import org.gbif.hbase.util.ResultReader;

import java.io.IOException;
import javax.annotation.Nullable;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A convenience class that wraps an HBase table and provides typed get and put operations.
 *
 * @param <T> the type of the HBase table's key
 */
public class HBaseStore<T> {

  private static final Logger LOG = LoggerFactory.getLogger(HBaseStore.class);

  private final String tableName;
  private final String cf;
  private final byte[] cfBytes;
  private final HTablePool tablePool;

  // TODO consider a put and get builder that adds columns with successive calls

  public HBaseStore(String tableName, String cf, HTablePool tablePool) {
    this.tableName = checkNotNull(tableName, "tableName can't be null");
    this.cf = checkNotNull(cf, "cf can't be null");
    this.cfBytes = Bytes.toBytes(cf);
    this.tablePool = checkNotNull(tablePool, "tablePool can't be null");
  }

  public Integer getInt(T key, String columnName) {
    Result row = getRow(key, columnName);
    return ResultReader.getInteger(row, cf, columnName, null);
  }

  public Long getLong(T key, String columnName) {
    Result row = getRow(key, columnName);
    return ResultReader.getLong(row, cf, columnName, null);
  }

  public Double getDouble(T key, String columnName) {
    Result row = getRow(key, columnName);
    return ResultReader.getDouble(row, cf, columnName, null);
  }

  public Float getFloat(T key, String columnName) {
    Result row = getRow(key, columnName);
    return ResultReader.getFloat(row, cf, columnName, null);
  }

  public String getString(T key, String columnName) {
    Result row = getRow(key, columnName);
    return ResultReader.getString(row, cf, columnName, null);
  }

  public byte[] getBytes(T key, String columnName) {
    Result row = getRow(key, columnName);
    return ResultReader.getBytes(row, cf, columnName, null);
  }

  public void putInt(T key, String columnName, int value) {
    put(key, columnName, Bytes.toBytes(value));
  }

  public void putLong(T key, String columnName, long value) {
    put(key, columnName, Bytes.toBytes(value));
  }

  public void putFloat(T key, String columnName, float value) {
    put(key, columnName, Bytes.toBytes(value));
  }

  public void putDouble(T key, String columnName, double value) {
    put(key, columnName, Bytes.toBytes(value));
  }

  public void putString(T key, String columnName, String value) {
    put(key, columnName, Bytes.toBytes(value));
  }

  public long incrementColumnValue(T key, String columnName, long value) {
    checkNotNull(key, "key can't be null");
    checkNotNull(columnName, "columnName can't be null");
    checkNotNull(value, "value can't be null");

    long result = 0;
    HTableInterface table = null;
    try {
      table = tablePool.getTable(tableName);
      byte[] byteKey = convertKey(key);
      if (byteKey != null) {
        result = table.incrementColumnValue(byteKey, cfBytes, Bytes.toBytes(columnName), value);
      }
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not read from HBase", e);
    } finally {
      try {
        if (table != null) {
          table.close();
        }
      } catch (IOException e) {
        LOG.warn("Couldn't return table to pool - continuing with possible memory leak", e);
      }
    }

    return result;
  }

  private void put(T key, String columnName, byte[] value) {
    checkNotNull(key, "key can't be null");
    checkNotNull(columnName, "columnName can't be null");
    checkNotNull(value, "value can't be null");

    HTableInterface table = null;
    try {
      table = tablePool.getTable(tableName);
      byte[] byteKey = convertKey(key);
      if (byteKey != null) {
        Put put = new Put(byteKey);
        put.add(cfBytes, Bytes.toBytes(columnName), value);
        table.put(put);
        table.flushCommits();
      }
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not read from HBase", e);
    } finally {
      try {
        if (table != null) {
          table.close();
        }
      } catch (IOException e) {
        LOG.warn("Couldn't return table to pool - continuing with possible memory leak", e);
      }
    }
  }

  /**
   * Returns an HBase Result object matching the given key and column name.
   *
   * @param key the primary key of the requested row
   * @param columnName the column value to return
   * @return HBase Result
   *
   * @throws ServiceUnavailableException if there are errors when communicating with HBase
   */
  public Result getRow(T key, String columnName) {
    checkNotNull(key, "key can't be null");
    checkNotNull(columnName, "columnName can't be null");

    HTableInterface table = null;
    Result row = null;
    try {
      table = tablePool.getTable(tableName);
      byte[] byteKey = convertKey(key);
      if (byteKey != null) {
        Get get = new Get(byteKey);
        get.addColumn(cfBytes, Bytes.toBytes(columnName));
        row = table.get(get);
      }
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not read from HBase", e);
    } finally {
      try {
        if (table != null) {
          table.close();
        }
      } catch (IOException e) {
        LOG.warn("Couldn't return table to pool - continuing with possible memory leak", e);
      }
    }

    return row;
  }

  /**
   * Returns an HBase Result object matching the given key.
   *
   * @param key the primary key of the requested row
   * @return HBase Result
   *
   * @throws ServiceUnavailableException if there are errors when communicating with HBase
   */
  @Nullable
  public Result getRow(T key) {
    checkNotNull(key, "key can't be null");

    HTableInterface table = null;
    Result row = null;
    try {
      table = tablePool.getTable(tableName);
      byte[] byteKey = convertKey(key);
      if (byteKey != null) {
        Get get = new Get(byteKey);
        row = table.get(get);
      }
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not read from HBase", e);
    } finally {
      try {
        if (table != null) {
          table.close();
        }
      } catch (IOException e) {
        LOG.warn("Couldn't return table to pool - continuing with possible memory leak", e);
      }
    }

    return row;
  }

  /**
   * Do an HBase checkAndPut - a put that will only be attempted if the checkColumn contains the expected checkValue.
   *
   * @param key the primary key of the row
   * @param putColumn the column where the new value will be stored
   * @param putValue the new value to put
   * @param checkColumn the column to check
   * @param checkValue the expected value of the checkColumn
   * @param ts the timestamp to write on the put (if null, the current timestamp will be used)
   * @return true if condition was met and put was successful, false otherwise
   *
   * @throws ServiceUnavailableException if there are errors when communicating with HBase
   */
  public boolean checkAndPut(T key, String putColumn, byte[] putValue, String checkColumn, @Nullable byte[] checkValue,
    @Nullable Long ts) {
    checkNotNull(key, "key can't be null");
    checkNotNull(putColumn, "putColumn can't be null");
    checkNotNull(putValue, "putValue can't be null");
    checkNotNull(checkColumn, "checkColumn can't be null");

    boolean success = false;
    HTableInterface table = null;
    try {
      table = tablePool.getTable(tableName);
      byte[] byteKey = convertKey(key);
      if (byteKey != null) {
        Put put = new Put(byteKey);
        if (ts != null && ts > 0) {
          put.add(cfBytes, Bytes.toBytes(putColumn), ts, putValue);
        } else {
          put.add(cfBytes, Bytes.toBytes(putColumn), putValue);
        }
        success = table.checkAndPut(byteKey, cfBytes, Bytes.toBytes(checkColumn), checkValue, put);
      }
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not read from HBase", e);
    } finally {
      try {
        if (table != null) {
          table.close();
        }
      } catch (IOException e) {
        LOG.warn("Couldn't return table to pool - continuing with possible memory leak", e);
      }
    }

    return success;
  }

  // TODO: fix deletions generally and add javadoc
  public void delete(T key, String... columns) {
    checkNotNull(key, "key can't be null");
    checkArgument(columns.length > 0, "columns can't be empty");

    HTableInterface table = null;
    try {
      table = tablePool.getTable(tableName);
      byte[] byteKey = convertKey(key);
      if (byteKey != null) {
        Delete delete = new Delete(byteKey);
        for (String column : columns) {
          delete.deleteColumn(cfBytes, Bytes.toBytes(column));
        }
        table.delete(delete);
        table.flushCommits();
      }
    } catch (IOException e) {
      throw new ServiceUnavailableException("Could not read from HBase", e);
    } finally {
      try {
        if (table != null) {
          table.close();
        }
      } catch (IOException e) {
        LOG.warn("Couldn't return table to pool - continuing with possible memory leak", e);
      }
    }
  }

  private byte[] convertKey(T key) {
    // instanceof is dirty, but it's that or separate classes for different key types
    if (key instanceof Integer) {
      return Bytes.toBytes((Integer) key);
    } else if (key instanceof String) {
      return Bytes.toBytes((String) key);
    } else if (key instanceof Long) {
      return Bytes.toBytes((Long) key);
    } else if (key instanceof Float) {
      return Bytes.toBytes((Float) key);
    } else if (key instanceof Double) {
      return Bytes.toBytes((Double) key);
    }

    return null;
  }
}
