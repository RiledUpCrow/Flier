package pl.betoncraft.flier.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Saver extends Thread {
	private ConcurrentLinkedQueue<Record> queue = new ConcurrentLinkedQueue<>();
	private boolean run = true;

	public Saver() {
		start();
	}

	public void run() {
		for (;;) {
			if (!this.run) {
				return;
			}
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (!this.queue.isEmpty()) {
				Record rec = (Record) this.queue.poll();
				try {
					for (int i = 0; i < rec.args.length; i++) {
						rec.update.setObject(i + 1, rec.args[i]);
					}
					rec.update.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void add(PreparedStatement update, Object[] args) {
		this.queue.add(new Record(update, args));
		notify();
	}

	public synchronized void end() {
		this.run = false;
		notify();
	}

	private static class Record {
		private PreparedStatement update;
		private Object[] args;

		private Record(PreparedStatement update, Object[] args) {
			this.update = update;
			this.args = args;
		}
	}
}
