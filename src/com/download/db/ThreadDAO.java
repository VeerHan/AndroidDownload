package com.download.db;

import java.util.List;

import com.download.entities.ThreadInfo;

/**
 * ���ݷ��ʽӿ�
 * 
 * @author Leo
 */
public interface ThreadDAO {
	/**
	 * �����߳���Ϣ
	 * 
	 * @param threadInfo
	 * @return void
	 * @author Leo
	 */
	public void insertThread(ThreadInfo threadInfo);

	/**
	 * ɾ���߳���Ϣ
	 * 
	 * @param url
	 * @param thread_id
	 * @return void
	 * @author Leo
	 */
	public void deleteThread(String url, int thread_id);

	/**
	 * �����߳����ؽ���
	 * 
	 * @param url
	 * @param thread_id
	 * @return void
	 * @author Leo
	 */
	public void updateThread(String url, int thread_id, int finished);

	/**
	 * ��ѯ�ļ����߳���Ϣ
	 * 
	 * @param url
	 * @return
	 * @return List<ThreadInfo>
	 * @author Leo
	 */
	public List<ThreadInfo> getThreads(String url);

	/**
	 * �߳���Ϣ�Ƿ����
	 * 
	 * @param url
	 * @param thread_id
	 * @return
	 * @return boolean
	 * @author Leo
	 */
	public boolean isExists(String url, int thread_id);
}
