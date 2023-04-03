package simpledb.tx.recovery;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import simpledb.file.BlockId;
import simpledb.log.LogMgr;
import simpledb.buffer.BufferMgr;
import simpledb.buffer.Buffer;
import simpledb.tx.Transaction;
import static simpledb.tx.recovery.LogRecord.START;
import static simpledb.tx.recovery.LogRecord.COMMIT;
import static simpledb.tx.recovery.LogRecord.CHECKPOINT;
import static simpledb.tx.recovery.LogRecord.ROLLBACK;

/**
 * The recovery manager. Each transaction has its own recovery manager.
 * 
 * @author Edward Sciore
 */
public class RecoveryMgr {
  private LogMgr lm;
  private BufferMgr bm;
  private Transaction tx;
  private int txnum;

  /**
   * Create a recovery manager for the specified transaction.
   * 
   * @param txnum the ID of the specified transaction
   */
  public RecoveryMgr(Transaction tx, int txnum, LogMgr lm, BufferMgr bm) {
    this.tx = tx;
    this.txnum = txnum;
    this.lm = lm;
    this.bm = bm;
    StartRecord.writeToLog(lm, txnum);
  }

  /**
   * Write a commit record to the log, and flushes it to disk.
   */
  public void commit() {
    bm.flushAll(txnum);
    int lsn = CommitRecord.writeToLog(lm, txnum);
    lm.flush(lsn);
  }

  /**
   * Write a rollback record to the log and flush it to disk.
   */
  public void rollback() {
    doRollback();
    bm.flushAll(txnum);
    int lsn = RollbackRecord.writeToLog(lm, txnum);
    lm.flush(lsn);
  }

  /**
   * Recover uncompled transactions from the log
   * and the write a quescent checkpont record to the log and flush it.
   */
  public void recover() {
    doRecover();
    bm.flushAll(txnum);
    int lsn = CheckpointRecord.writeToLog(lm);
    lm.flush(lsn);
  }

  /**
   * Write a setint record to the log and return its lsn.
   * 
   * @param buff   the buffer containing the page
   * @param offset the offset of the value in the page
   * @param newval the value to be written
   */
  public int setInt(Buffer buff, int offset, int newval) {
    int oldval = buff.contents().getInt(offset);
    BlockId blk = buff.block();
    return SetIntRecord.writeToLog(lm, newval, blk, offset, oldval);
  }

  /**
   * Write a setstring record to the log and return its lsn.
   * 
   * @param buff   the buffer containing the page
   * @param offset the offset of the value in the page
   * @param newval the value to be written
   */
  public int setString(Buffer buff, int offset, String newval) {
    String oldval = buff.contents().getString(offset);
    BlockId blk = buff.block();
    return SetStringRecord.wirteToLog(lm, txnum, blk, offset, oldval);
  }

  /**
   * Rollback the transaction, by iterating
   * through the log records until it finds
   * the transaction's START record,
   * calling undo() for each of the transaction's
   * log records.
   */
  private void doRollback() {
    Iterator<byte[]> iter = lm.iterator();
    while (iter.hasNext()) {
      byte[] bytes = iter.next();
      LogRecord rec = LogRecord.createLogRecord(bytes);
      if (rec.txNumber() == txnum) {
        if (rec.op() == START)
          return;
        rec.undo(tx);
      }
    }
  }

  /**
   * Do a comoplete database recovery.
   * The method iterates through the log records.
   * Whenever it finds a log record for an unfinished
   * transaction, it calls undo() on that record.
   * The method stops when it encounters a CHEKPOINT record
   * of the end of the log.
   */
  private void doRecover() {
    Collection<Integer> finishedTxs = new ArrayList<>();
    Iterator<byte[]> iter = lm.iterator();
    while (iter.hasNext()) {
      byte[] bytes = iter.next();
      LogRecord rec = LogRecord.createLogRecord(bytes);
      if (rec.op() == CHECKPOINT)
        return;
      if (rec.op() == COMMIT || rec.op() == ROLLBACK)
        finishedTxs.add(rec.txNumber());
      else if (!finishedTxs.contains(rec.txNumber()))
        rec.undo(tx);
    }
  }
}
