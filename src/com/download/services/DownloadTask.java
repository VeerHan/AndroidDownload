package com.download.services;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpStatus;

import com.download.app.MainActivity;
import com.download.db.ThreadDAO;
import com.download.db.ThreadDAOImpl;
import com.download.entities.FileInfo;
import com.download.entities.ThreadInfo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ����������
 * 
 * @author Leo
 */
public class DownloadTask {
	private Context mContext = null;
	private FileInfo mFileInfo = null;
	private ThreadDAO mDao = null;
	private int mFinised = 0;
	public boolean isPause = false;

	/**
	 * @param mContext
	 * @param mFileInfo
	 */
	public DownloadTask(Context mContext, FileInfo mFileInfo) {
		this.mContext = mContext;
		this.mFileInfo = mFileInfo;
		mDao = new ThreadDAOImpl(mContext);
	}

	public void downLoad() {
		// ��ȡ���ݿ���߳���Ϣ
		List<ThreadInfo> threads = mDao.getThreads(mFileInfo.getUrl());
		ThreadInfo threadInfo = null;

		if (0 == threads.size()) {
			// ��ʼ���߳���Ϣ����
			threadInfo = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0);
		} else {
			threadInfo = threads.get(0);
		}

		// �������߳̽�������
		new DownloadThread(threadInfo).start();
	}

	/**
	 * �����߳�
	 * 
	 * @author Leo
	 * @date 2015-8-8 ����11:18:55
	 */
	private class DownloadThread extends Thread {
		private ThreadInfo mThreadInfo = null;

		/**
		 * @param mInfo
		 */
		public DownloadThread(ThreadInfo mInfo) {
			this.mThreadInfo = mInfo;
		}

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// �����ݿ�����߳���Ϣ
			if (!mDao.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())) {
				mDao.insertThread(mThreadInfo);
			}

			HttpURLConnection connection = null;
			RandomAccessFile raf = null;
			InputStream inputStream = null;

			try {
				URL url = new URL(mThreadInfo.getUrl());
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				// ��������λ��
				int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
				connection.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
				// �����ļ�д��λ��
				File file = new File(DownloadService.DOWNLOAD_PATH, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				Intent intent = new Intent();
				intent.setAction(DownloadService.ACTION_UPDATE);
				mFinised += mThreadInfo.getFinished();
				// ��ʼ����
				if (connection.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
					// ��ȡ����
					inputStream = connection.getInputStream();
					byte buf[] = new byte[1024 << 2];
					int len = -1;
					long time = System.currentTimeMillis();
					while ((len = inputStream.read(buf)) != -1) {
						// д���ļ�
						raf.write(buf, 0, len);
						// �����ؽ��ȷ��͹㲥��Activity
						mFinised += len;
						if (System.currentTimeMillis() - time > 500) {
							time = System.currentTimeMillis();
							intent.putExtra("finished", mFinised * 100 / mThreadInfo.getEnd());
							mContext.sendBroadcast(intent);
						}

						// ��������ͣʱ���������ؽ���
						if (isPause) {
							mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinised);
							return;
						}
					}

					// ɾ���߳���Ϣ
					mDao.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getId());
					Log.i("DownloadTask", "�������");
					MainActivity.mMainActivity.handler.sendEmptyMessage(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (connection != null) {
						connection.disconnect();
					}
					if (raf != null) {
						raf.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
}